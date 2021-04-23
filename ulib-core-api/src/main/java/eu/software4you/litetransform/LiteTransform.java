package eu.software4you.litetransform;

import eu.software4you.ulib.Await;

import java.lang.reflect.Method;

/**
 * Injects methods marked with {@link Hook}.
 */
public abstract class LiteTransform {
    @Await
    private static LiteTransform impl;

    /**
     * Searches a class (and it's superclasses) for any hooks and tries to inject them.
     *
     * @throws NoSuchMethodError if no default constructor exists and the hook method is not static.
     */
    public static void autoHook(Class<?> clazz) {
        impl.autoHook0(clazz);
    }

    /**
     * Hooks a method directly into a specific class
     *
     * @param source           the method to hook
     * @param obj              the instance object, ignored on static methods
     * @param methodName       the name of the target method
     * @param methodDescriptor the signature of the target method
     * @param className        the fully qualified class name of the declaring class
     * @param at               the hook point
     */
    public static void hook(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at) {
        impl.hook0(source, obj, methodName, methodDescriptor, className, at);
    }

    /**
     * Hooks a source method into another. Useful for dynamic injection.
     *
     * @param source the method to hook
     * @param obj    the instance object, ignored on static methods
     * @param into   the method to be injected in
     * @param at     the point where to inject
     */
    public static void hook(Method source, Object obj, Method into, HookPoint at) {
        impl.hook0(source, obj, into, at);
    }

    protected abstract void autoHook0(Class<?> clazz);

    protected abstract void hook0(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at);

    protected abstract void hook0(Method source, Object obj, Method into, HookPoint at);
}
