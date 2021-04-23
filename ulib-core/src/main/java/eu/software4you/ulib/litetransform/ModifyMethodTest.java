package eu.software4you.ulib.litetransform;

import javassist.*;

import java.util.regex.Pattern;

public class ModifyMethodTest {

    /**
     * Creates a new ModifyMethodTest
     *
     * @param className       The internal form class name to modify
     * @param methodName      The name of the method to transform
     * @param methodSignature A regular expression to match the method signature. (if null, matches ".*")
     * @param classLoader     The intrumentation provided classloader
     * @param byteCode        The pre-transform byte code
     * @return the modified byte code if successful, otherwise returns the original unmodified byte code
     */
    public static byte[] instrument(String className, String methodName, String methodSignature, ClassLoader classLoader, byte[] byteCode) {
        String binName = className.replace('/', '.');
        try {
            ClassPool cPool = new ClassPool(true);
            cPool.appendClassPath(new LoaderClassPath(classLoader));
            cPool.appendClassPath(new ByteArrayClassPath(binName, byteCode));
            CtClass ctClazz = cPool.get(binName);
            Pattern sigPattern = Pattern.compile((methodSignature == null || methodSignature.trim().isEmpty()) ? ".*" : methodSignature);
            int modifies = 0;
            for (CtMethod method : ctClazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    if (sigPattern.matcher(method.getSignature()).matches()) {
                        ctClazz.removeMethod(method);
                        String newCode = "System.out.println(\"\\n\\t-->Invoked method [" + binName + "." + method.getName() + "(" + method.getSignature() + ")]\");";
                        System.out.println("[ModifyMethodTest] Adding [" + newCode + "]");
                        method.insertBefore(newCode);
                        ctClazz.addMethod(method);
                        modifies++;
                    }
                }
            }

            System.out.println("[ModifyMethodTest] Intrumented [" + modifies + "] methods");
            return ctClazz.toBytecode();
        } catch (Exception ex) {
            System.err.println("Failed to compile retransform class [" + binName + "] Stack trace follows...");
            ex.printStackTrace(System.err);
            return byteCode;
        }
    }

}
