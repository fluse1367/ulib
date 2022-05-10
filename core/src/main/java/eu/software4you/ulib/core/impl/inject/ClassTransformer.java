package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.HookPoint;
import eu.software4you.ulib.core.util.Expect;
import javassist.*;
import javassist.expr.*;
import lombok.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static eu.software4you.ulib.core.inject.HookPoint.*;

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

            try {
                // obtain behavior target
                var behavior = Optional.ofNullable(find(clazz, descriptor))
                        // indicate error if behavior cannot get obtained
                        .orElseThrow(() -> new NoSuchElementException("Descriptor %s not found in %s".formatted(descriptor, name)));

                // inject proxies
                injectProxies(classBeingRedefined, behavior);

                // inject hooks
                injectHookCalls(behavior);
            } catch (Throwable thr) {
                man.getTransformThrowings().computeIfPresent(Thread.currentThread(), (t, old) -> thr);
                return null;
            }

            count++;
        }


        try {
            return count == 0 ? null : clazz.toBytecode();
        } catch (Throwable thr) {
            man.getTransformThrowings().computeIfPresent(Thread.currentThread(), (t, old) -> thr);
            return null;
        }
    }

    private CtBehavior find(CtClass clazz, String descriptor) {
        var resolved = InjectionSupport.splitSignature(descriptor);
        String resolvedName = resolved.getFirst(), resolvedDescriptor = resolved.getSecond();

        if (resolvedName.equals("<init>")) {
            if (resolvedDescriptor.isBlank()) {
                // default constructor
                return clazz.getDeclaredConstructors()[0];
            }

            // actually resolve constructor
            return Expect.compute(clazz::getConstructor, resolvedDescriptor)
                    .orElse(null); // constructor not found
        }

        return Expect.compute(clazz::getMethod, resolvedName, resolvedDescriptor)
                .orElse(null); // method not found
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

        for (HookPoint hookPoint : new HookPoint[]{HookPoint.HEAD, HookPoint.RETURN}) {
            boolean head = hookPoint == HookPoint.HEAD;

            String returnValue = hasReturnType && !head ? ("(Object) " + (primitive ? "($w) " : "") + "$_") : "null";
            //noinspection WrapperTypeMayBePrimitive
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

    @SneakyThrows
    private String buildProxyInjection(final CtBehavior box, final CtClass returnType, final HookPoint where, final String fullTargetSignature, final int n) {
        var boxSignature = box.getName() + box.getSignature();

        return String.format("""
                        {
                          Object[] arr = (Object[]) System.getProperties().get("%s");
                          Function funcProxyRunner = (Function) arr[0];
                          Function funcIsReturning = (Function) arr[1];
                          Function funcHasReturnValue = (Function) arr[2];
                          Function funcGetReturnValue = (Function) arr[3];
                          Supplier funcDetermineCaller = (Supplier) arr[4];
                          
                          Object caller = funcDetermineCaller.get();
                          Object[] params = {%s.class, %s, %s, $0, caller, "%s", "%s", Integer.valueOf(%d), Integer.valueOf(%d), $args};
                          Object callback = funcProxyRunner.apply((Object) params);
                          Boolean isReturning = (Boolean) funcIsReturning.apply(callback);
                          
                          if (isReturning.booleanValue()) {
                            Boolean hasVal = (Boolean) funcHasReturnValue.apply(callback);
                            
                            if (hasVal.booleanValue()) {
                                $_ = ($r) funcGetReturnValue.apply((Object) callback);
                            }
                          } else {
                            $_ = $proceed(%s);
                          }
                        }""",
                InjectionManager.PROXY_KEY,
                /* return type */   returnType == CtClass.voidType ? "void" : returnType.getName(),
                /* initial value */ where == METHOD_CALL || where == FIELD_READ ? "null, Boolean.FALSE" : "$1, Boolean.TRUE",
                /* self */          Modifier.isStatic(box.getModifiers()) ? "null" : "this",
                boxSignature, fullTargetSignature, n, where.ordinal(),
                /* proceed */       where == FIELD_READ ? "" : "$$"
        );
    }

    private void injectProxies(Class<?> cl, CtBehavior behavior) throws CannotCompileException {
        final var boxSignature = behavior.getName() + behavior.getSignature();

        final Map<String, AtomicInteger> methodCallNs = new HashMap<>();
        final Map<String, AtomicInteger> fieldReadNs = new HashMap<>();
        final Map<String, AtomicInteger> fieldWriteNs = new HashMap<>();

        behavior.instrument(new ExprEditor() {

            @SneakyThrows
            @Override
            public void edit(MethodCall m) throws CannotCompileException {
                final String fullTargetSignature = "L%s;".formatted(m.getClassName().replace(".", "/")) +
                                                   m.getMethodName() + m.getSignature();

                int n = methodCallNs.computeIfAbsent(fullTargetSignature, sig -> new AtomicInteger(0))
                        .incrementAndGet(); // increment method occurrence counter
                if (!man.shouldProxy(cl, boxSignature, METHOD_CALL, fullTargetSignature, n))
                    return;

                m.replace(buildProxyInjection(m.where(), m.getMethod().getReturnType(), METHOD_CALL, fullTargetSignature, n));
            }

            @SneakyThrows
            @Override
            public void edit(FieldAccess f) throws CannotCompileException {
                HookPoint where;
                if (f.isReader()) {
                    where = FIELD_READ;
                } else if (f.isWriter()) {
                    where = FIELD_WRITE;
                } else {
                    throw new InternalError();
                }

                final String fullTargetSignature = "L%s;".formatted(f.getClassName().replace(".", "/")) +
                                                   f.getFieldName() + ";" + f.getSignature();

                int n = (where == FIELD_READ ? fieldReadNs : fieldWriteNs)
                        .computeIfAbsent(f.getSignature(), sig -> new AtomicInteger(0))
                        .incrementAndGet();

                if (!man.shouldProxy(cl, boxSignature, where, fullTargetSignature, n))
                    return;

                f.replace(buildProxyInjection(f.where(), f.getField().getType(), where, fullTargetSignature, n));
            }

        });
    }
}
