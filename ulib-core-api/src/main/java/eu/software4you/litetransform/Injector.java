package eu.software4you.litetransform;

import java.lang.reflect.Method;

/**
 * Injects methods marked with {@link Hook}.
 */
public interface Injector {

    /**
     * Searches a class (and it's superclasses) for any hooks and tries to inject them.
     *
     * @throws NoSuchMethodError if no default constructor exists and the hook method is not static.
     */
    void scan(Class<?> clazz);

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
    void hook(Method source, Object obj, String methodName, String methodDescriptor, String className, HookPoint at);

    /**
     * Hooks a source method into another. Useful for dynamic injection.
     *
     * @param source the method to hook
     * @param obj    the instance object, ignored on static methods
     * @param into   the method to be injected in
     * @param at     the point where to inject
     */
    void hook(Method source, Object obj, Method into, HookPoint at);
}
