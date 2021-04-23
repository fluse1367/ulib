package eu.software4you.ulib.impl.litetransform;

import eu.software4you.litetransform.HookPoint;
import javassist.*;
import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;

// Created with the help of: https://stackoverflow.com/a/18576798/8400001 and https://gist.github.com/nickman/6494990
final class Transformer implements ClassFileTransformer {
    private final Method source;
    private final Object obj;

    private final String className;
    private final String methodName;
    private final String methodDescriptor;
    private final HookPoint at;

    @SneakyThrows
    Transformer(Method source, Object obj, String className, String methodName, String methodDescriptor, HookPoint at) {
        this.source = source;
        this.obj = obj;

        this.className = className;
        this.at = at;
        this.methodName = methodName;
        this.methodDescriptor = methodDescriptor;
    }

    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        if (!clName.replace('/', '.').equals(this.className))
            return byteCode;

        try {
            ClassPool pool = new ClassPool(true); // ClassPool.getDefault()?
            pool.appendClassPath(new LoaderClassPath(loader));
            pool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            CtClass ctClazz = pool.get(className);
            CtMethod method = ctClazz.getMethod(methodName, methodDescriptor);


            ctClazz.removeMethod(method);

            // insert

            pool.importPackage("eu.software4you.ulib.impl.litetransform");
            boolean head = at == HookPoint.HEAD;
            boolean hasReturnType = method.getReturnType() != CtClass.voidType;
            boolean primitive = method.getReturnType().isPrimitive();
            boolean stat = Modifier.isStatic(method.getModifiers());
            int hookId = Hooks.addHook(source, obj);


            String returnType = hasReturnType ? method.getReturnType().getName() : "void";
            String returnValue = hasReturnType && !head ? ("(Object) " + (primitive ? "($w) " : "") + "$_") : "null";
            boolean hasReturnValue = !head && hasReturnType;
            String self = stat ? "null" : "$0";

            String src = String.format(
                    "Callback cb = new Callback(%s.class, %s, %s, %s);%n" +
                            "Hooks.runHook(%d, $args, cb);" +
                            "if (cb.%s()) { return%s; }",
                    returnType, returnValue, hasReturnValue, self,
                    hookId,
                    hasReturnType ? "hasReturnValue" : "isCanceled", hasReturnType ? " ($r) cb.getReturnValue()" : ""
            );

            if (head) {
                method.insertBefore(src);
            } else {
                method.insertAfter(src);
            }

            ctClazz.addMethod(method);

            return ctClazz.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }
}
