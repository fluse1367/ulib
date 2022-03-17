package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.HookPoint;
import eu.software4you.ulib.core.util.Expect;
import javassist.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public final class ClassTransformer implements ClassFileTransformer {

    private final InjectionManager man;

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!man.shouldProcess(classBeingRedefined))
            return null;

        final String name = classBeingRedefined.getName();

        ClassPool pool = new ClassPool(true);
        pool.appendClassPath(new LoaderClassPath(loader));
        pool.appendClassPath(new ByteArrayClassPath(name, classfileBuffer));

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

            Expect.compute(() -> injectHookCalls(object));
            count++;
        }

        return count == 0 ? null : Expect.compute(() -> clazz.toBytecode()).toOptional().orElse(null);
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
            boolean hasReturnValue = !head && hasReturnType;

            int at = hookPoint.ordinal();
            String src = String.format("""
                            {
                              Object[] arr = (Object[]) System.getProperties().remove("ulib.hooking");
                              Function funcHookRunner = (Function) arr[0];
                              Function funcIsReturning = (Function) arr[1];
                              Function funcGetReturnValue = (Function) arr[2];
                              Supplier funcDetermineCaller = (Supplier) arr[3];
                              
                              Object caller = funcDetermineCaller.get();
                              Object callback = funcHookRunner.apply((Object) new Object[]{$class, %s.class, %s, %s, %s, caller, "%s", %d, $args});
                              
                              if ((boolean) funcIsReturning.apply((Object) callback)) return%s;
                            }""",
                    /*Hook call*/ returnType, returnValue, hasReturnValue, self, /*hookId*/ methodDescriptor, at,
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
