package eu.software4you.ulib.impl.transform;

import eu.software4you.transform.HookPoint;
import javassist.*;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

// Created with the help of: https://stackoverflow.com/a/18576798/8400001 and https://gist.github.com/nickman/6494990
final class Transformer implements ClassFileTransformer {
    private final String className;
    private final List<String> methods; // methodName methodDescriptor

    @SneakyThrows
    Transformer(String className, List<String> methods) {
        this.className = className;
        this.methods = methods;
    }

    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        if (!clName.replace('/', '.').equals(this.className))
            return byteCode;

        try {
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(loader));
            pool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            pool.importPackage("eu.software4you.ulib.impl.transform");


            CtClass cc = pool.get(className);

            for (String desc : methods) {
                val pair = Util.resolveMethod(desc);
                String methodName = pair.getFirst();
                String methodDescriptor = pair.getSecond();

                CtMethod method = methodDescriptor.isEmpty() ? cc.getDeclaredMethod(methodName) : cc.getMethod(methodName, methodDescriptor);

                String fullDesc = Util.fullDescriptor(method);

                // inject hook call

                cc.removeMethod(method);


                boolean hasReturnType = method.getReturnType() != CtClass.voidType;
                boolean primitive = method.getReturnType().isPrimitive();
                String returnType = hasReturnType ? method.getReturnType().getName() : "void";
                String self = Modifier.isStatic(method.getModifiers()) ? "null" : "$0";

                for (HookPoint at : HookPoint.values()) {
                    boolean head = at == HookPoint.HEAD;

                    String returnValue = hasReturnType && !head ? ("(Object) " + (primitive ? "($w) " : "") + "$_") : "null";
                    boolean hasReturnValue = !head && hasReturnType;

                    String src = String.format(
                            "{ Callback cb = new Callback(%s.class, %s, %s, %s);%n" +
                                    "Hooks.runHooks(\"%s\", %s, $args, cb);" +
                                    "if (cb.%s()) return%s; }",
                            returnType, returnValue, hasReturnValue, self,
                            fullDesc, HookPoint.class.getName() + "." + at.name(),
                            hasReturnType ? "hasReturnValue" : "isCanceled", hasReturnType ? " ($r) cb.getReturnValue()" : ""
                    );

                    switch (at) {
                        case HEAD:
                            method.insertBefore(src);
                            break;
                        case RETURN:
                            method.insertAfter(src);
                            break;
                    }
                }

                cc.addMethod(method);
            }

            return cc.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }
}
