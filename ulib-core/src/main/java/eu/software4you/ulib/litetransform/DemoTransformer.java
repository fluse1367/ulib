package eu.software4you.ulib.litetransform;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class DemoTransformer implements ClassFileTransformer {
    /**
     * The normal form class name of the class to transform
     */
    protected String className;
    /**
     * The class loader of the class
     */
    protected ClassLoader classLoader;
    /**
     * The method name
     */
    protected String methodName;
    /**
     * The method signature
     */
    protected String methodSignature;

    /**
     * Creates a new DemoTransformer
     *
     * @param classLoader     The classloader to match
     * @param className       The binary class name of the class to transform
     * @param methodName      The method name
     * @param methodSignature A regular expression matching the method signature
     */
    public DemoTransformer(ClassLoader classLoader, String className, String methodName, String methodSignature) {
        this.className = className.replace('.', '/');
        this.classLoader = classLoader;
        this.methodName = methodName;
        this.methodSignature = methodSignature;
    }

    /**
     * {@inheritDoc}
     *
     * @see ClassFileTransformer#transform(ClassLoader, String, Class, ProtectionDomain, byte[])
     */
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) {
        System.out.println("Examining class [" + className + "]");
        if (className.equals(this.className) && loader.equals(classLoader)) {
            System.out.println("Instrumenting class [" + className + "]");
            return ModifyMethodTest.instrument(className, methodName, methodSignature, loader, classfileBuffer);
        }
        return classfileBuffer;
    }

}