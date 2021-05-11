package eu.software4you.ulib.impl.transform;

import eu.software4you.transform.HookPoint;
import javassist.*;
import lombok.val;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

// Created with the help of: https://stackoverflow.com/a/18576798/8400001 and https://gist.github.com/nickman/6494990
final class Transformer implements ClassFileTransformer {
    private final String className;
    private final List<String> methods; // methodName methodDescriptor
    private final Logger logger;

    Transformer(String className, List<String> methods, Logger logger) {
        this.className = className;
        this.methods = methods;
        this.logger = logger;

        this.logger.finest(() -> this + " init");
    }

    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        if (!clName.replace('/', '.').equals(this.className))
            return byteCode;

        logger.fine(() -> "Transforming " + className);

        try {
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(loader));
            pool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            pool.importPackage("eu.software4you.libex.function");

            CtClass cc = pool.get(className);
            logger.finest(cc::toString);

            for (String desc : methods) {
                logger.finer(() -> "Searching for " + desc);

                val pair = Util.resolveMethod(desc);
                String methodName = pair.getFirst();
                String methodDescriptor = pair.getSecond();

                CtMethod method;
                try {
                    logger.finest(() -> String.format("Class: %s Method: %s Descriptor: %s", cc.getName(), methodName, methodDescriptor));

                    method = methodDescriptor.isEmpty() ? cc.getDeclaredMethod(methodName) : cc.getMethod(methodName, methodDescriptor);
                } catch (NotFoundException e) {
                    logger.log(Level.WARNING, () -> "Hook injection failed: " + desc + " not found");
                    continue;
                }

                String fullDesc = Util.fullDescriptor(method);
                logger.fine(() -> "Visiting " + fullDesc);

                // inject hook call

                cc.removeMethod(method);


                boolean hasReturnType = method.getReturnType() != CtClass.voidType;
                boolean primitive = method.getReturnType().isPrimitive();
                String returnType = hasReturnType ? method.getReturnType().getName() : "void";
                String self = Modifier.isStatic(method.getModifiers()) ? "null" : "$0";

                for (HookPoint hookPoint : HookPoint.values()) {
                    logger.finer(() -> "Injecting hook call into " + fullDesc + " at " + hookPoint.name());

                    boolean head = hookPoint == HookPoint.HEAD;

                    String returnValue = hasReturnType && !head ? ("(Object) " + (primitive ? "($w) " : "") + "$_") : "null";
                    boolean hasReturnValue = !head && hasReturnType;

                    int at = hookPoint.ordinal();
                    String src = String.format("{\n" +
                                    "  Callb cb = new Callb(%s.class, %s, %s, %s, \"%s\", %d, $args);\n" +
                                    "  if (cb.isReturning()) return%s;\n" +
                                    "}",
                            /*Hook call*/ returnType, returnValue, hasReturnValue, self, /*hookId*/ fullDesc, at,
                            /*return*/ hasReturnType ? " ($r) cb.getReturnValue()" : ""
                    );

                    logger.finest(() -> "Compiling:\n\t" + src.replace("\n", "\n\t"));

                    switch (hookPoint) {
                        case HEAD:
                            method.insertBefore(src);
                            break;
                        case RETURN:
                            method.insertAfter(src);
                            break;
                    }

                    logger.finer(() -> "Injection done!");
                }

                cc.addMethod(method);
            }

            logger.fine(() -> "Transformation done!");
            return cc.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }
}
