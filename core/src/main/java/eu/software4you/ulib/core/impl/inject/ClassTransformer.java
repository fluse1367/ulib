package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.HookPoint;
import eu.software4you.ulib.core.util.Expect;
import javassist.*;
import lombok.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ClassTransformer implements ClassFileTransformer {

    private final InjectionManager man;

    @SneakyThrows
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!man.shouldProcess(classBeingRedefined))
            return null;

        final String name = classBeingRedefined.getName();

        ClassPool pool = new ClassPool(true);
        pool.appendClassPath(new LoaderClassPath(loader));
        pool.appendClassPath(new ByteArrayClassPath(name, classfileBuffer));
        pool.importPackage("java.util.function");

        CtClass clazz;
        {
            var res = Expect.compute(pool::get, name);
            if (res.isEmpty())
                return null;
            clazz = res.orElseThrow();
        }

        int count = 0;
        for (String descriptor : man.getTargetMethods(classBeingRedefined)) {
            var resolved = InjectionSupport.resolveMethod(descriptor);
            String resolvedName = resolved.getFirst(), resolvedDescriptor = resolved.getSecond();

            CtBehavior object;
            if (resolvedName.equals("<init>")) {
                if (resolvedDescriptor.isBlank()) {
                    object = clazz.getDeclaredConstructors()[0];
                } else {
                    var res = Expect.compute(clazz::getConstructor, resolvedDescriptor);
                    if (res.isEmpty())
                        continue;
                    object = res.orElseThrow();
                }
            } else {
                var res = Expect.compute(clazz::getMethod, resolvedName, resolvedDescriptor);
                if (res.isEmpty())
                    continue; // skip this method TODO: log error somehow?
                object = res.orElseThrow();
            }

            try {
                Expect.compute(() -> injectHookCalls(object)).rethrow();
            } catch (Throwable thr) {
                man.getTransformThrowings().computeIfPresent(Thread.currentThread(), (t, old) -> thr);
                return null;
            }

            count++;
        }


        try {
            return count == 0 ? null : Expect.compute(() -> clazz.toBytecode()).orElseRethrow();
        } catch (Throwable thr) {
            man.getTransformThrowings().computeIfPresent(Thread.currentThread(), (t, old) -> thr);
            return null;
        }
    }

    private void injectHookCalls(CtBehavior behavior) throws NotFoundException, CannotCompileException {
        String methodDescriptor = behavior.getName() + behavior.getSignature();
        CtClass ctReturnType;
        boolean isConstructor = false;
        if (behavior instanceof CtMethod ctm) {
            ctReturnType = ctm.getReturnType();
        } else {
            isConstructor = true;
            ctReturnType = null;
        }
        boolean hasReturnType = !isConstructor && ctReturnType != CtClass.voidType;
        boolean primitive = !isConstructor && ctReturnType.isPrimitive();
        String returnType = hasReturnType ? ctReturnType.getName() : "void";
        String self = !isConstructor && Modifier.isStatic(behavior.getModifiers()) ? "null" : "$0";

        for (HookPoint hookPoint : HookPoint.values()) {
            boolean head = hookPoint == HookPoint.HEAD;

            String returnValue = hasReturnType && !head ? ("(Object) " + (primitive ? "($w) " : "") + "$_") : "null";
            Boolean hasReturnValue = !head && hasReturnType;

            int at = hookPoint.ordinal();
            String src = String.format("""
                            {
                              Object[] arr = (Object[]) System.getProperties().get("%s");
                              Function funcHookRunner = (Function) arr[0];
                              Function funcIsReturning = (Function) arr[1];
                              Function funcGetReturnValue = (Function) arr[2];
                              Supplier funcDetermineCaller = (Supplier) arr[3];
                              
                              Object caller = funcDetermineCaller.get();
                              Object[] params = {%s.class, %s, Boolean.%s, %s, caller, "%s", Integer.valueOf(%d), $args};
                              Object callback = funcHookRunner.apply((Object) params);
                              Boolean isReturning = (Boolean) funcIsReturning.apply(callback);
                              
                              if (isReturning.booleanValue()) return%s;
                            }""",
                    InjectionManager.HOOKING_KEY,
                    /*Hook call*/ returnType, returnValue, hasReturnValue.toString().toUpperCase(), self, /*hookId*/ methodDescriptor, at,
                    /*return*/ hasReturnType ? " ($r) funcGetReturnValue.apply((Object) callback)" : ""
            );

            switch (hookPoint) {
                case HEAD -> {
                    if (behavior instanceof CtMethod ctm) {
                        ctm.insertBefore(src);
                    } else {
                        ((CtConstructor) behavior).insertBeforeBody(src);
                    }
                }
                case RETURN -> behavior.insertAfter(src);
            }
        }
    }
}
