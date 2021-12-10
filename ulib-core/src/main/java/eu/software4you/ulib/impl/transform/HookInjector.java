package eu.software4you.ulib.impl.transform;

import eu.software4you.ulib.core.api.transform.HookPoint;
import javassist.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

// Created with the help of: https://stackoverflow.com/a/18576798/8400001 and https://gist.github.com/nickman/6494990
final class HookInjector implements ClassFileTransformer {
    private final Logger logger;
    private final Map<String, List<String>> covering;

    HookInjector(Logger logger, Map<String, List<String>> covering) {
        this.logger = logger;
        this.covering = covering;

        this.logger.finest(() -> this + " init");
    }

    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        final String className;
        if (!covering.containsKey(className = clName.replace('/', '.')))
            return byteCode;

        logger.fine(() -> "Transforming " + className);

        try {
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(loader));
            pool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            pool.importPackage("eu.software4you.libex.function");

            CtClass cc = pool.get(className);
            logger.finest(cc::toString);

            for (final String desc : covering.get(className)) {
                logger.finer(() -> "Searching for " + desc);

                var pair = Util.resolveMethod(desc);
                String methodName = pair.getFirst();
                String methodDescriptor = pair.getSecond();

                boolean constr = methodName.equals("<init>");

                CtConstructor constructor = null;
                CtMethod method = null;
                CtBehavior ctb;
                try {
                    logger.finest(() -> String.format("Class: %s Method: %s Descriptor: %s", cc.getName(), methodName, methodDescriptor));

                    if (constr) {
                        ctb = constructor = methodDescriptor.isEmpty() ? cc.getDeclaredConstructors()[0] : cc.getConstructor(methodDescriptor);
                    } else {
                        ctb = method = methodDescriptor.isEmpty() ? cc.getDeclaredMethod(methodName) : cc.getMethod(methodName, methodDescriptor);
                    }
                } catch (NotFoundException e) {
                    logger.log(Level.WARNING, () -> "Hook injection failed: " + desc + " not found");
                    continue;
                }

                String fullDesc = String.format("%s.%s", clName, desc);
                logger.fine(() -> "Visiting " + fullDesc);

                // inject hook call

                if (constr) {
                    cc.removeConstructor(constructor);
                } else {
                    cc.removeMethod(method);
                }


                boolean hasReturnType = !constr && method.getReturnType() != CtClass.voidType;
                boolean primitive = !constr && method.getReturnType().isPrimitive();
                String returnType = hasReturnType ? method.getReturnType().getName() : "void";
                String self = !constr && Modifier.isStatic(method.getModifiers()) ? "null" : "$0";

                for (HookPoint hookPoint : HookPoint.values()) {
                    logger.finer(() -> "Injecting hook call into " + fullDesc + " at " + hookPoint.name());

                    boolean head = hookPoint == HookPoint.HEAD;

                    String returnValue = hasReturnType && !head ? ("(Object) " + (primitive ? "($w) " : "") + "$_") : "null";
                    boolean hasReturnValue = !head && hasReturnType;

                    int at = hookPoint.ordinal();
                    String src = String.format("""
                                    {
                                      Callb cb = new Callb(%s.class, %s, %s, %s, "%s", %d, $args);
                                      if (cb.isReturning()) return%s;
                                    }""",
                            /*Hook call*/ returnType, returnValue, hasReturnValue, self, /*hookId*/ fullDesc, at,
                            /*return*/ hasReturnType ? " ($r) cb.getReturnValue()" : ""
                    );

                    logger.finest(() -> "Compiling:\n\t" + src.replace("\n", "\n\t"));

                    switch (hookPoint) {
                        case HEAD:
                            if (constr) {
                                constructor.insertBeforeBody(src);
                            } else {
                                method.insertBefore(src);
                            }
                            break;
                        case RETURN:
                            ctb.insertAfter(src);
                            break;
                    }

                    logger.finer(() -> "Injection done!");
                }

                if (constr) {
                    cc.addConstructor(constructor);
                } else {
                    cc.addMethod(method);
                }
            }

            logger.fine(() -> "Transformation done!");
            return cc.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }
}
