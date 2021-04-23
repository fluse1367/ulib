package eu.software4you.litetransform.injection;

import java.lang.reflect.Method;

/**
 * Injects methods marked with {@link Inject}.
 */
public interface Injector {

    /**
     * Searches a class (and it's superclasses) for any injections and tries to inject them.
     */
    void injectFrom(Class<?> clazz);

    /**
     * Injects a method directly into a specific class
     *
     * @param source          the method to inject
     * @param methodName      the name of the target method
     * @param methodSignature the signature of the target method
     * @param clazz           the class of the target method
     * @param at              the injection point
     * @param ordinal         the ordinal, see {@link Inject#ordinal()}
     * @see Inject#ordinal()
     */
    void inject(Method source, String methodName, String methodSignature, String clazz, InjectionPoint at, int ordinal);

    /**
     * Injects a source method into another. Useful for dynamic injection.
     *
     * @param source the method to be injected
     * @param into   the method to be injected to
     * @param at     the point where to inject
     */
    default void inject(Method source, Method into, InjectionPoint at) {
        inject(source, into, at, -1);
    }

    /**
     * Injects a source method into another. Useful for dynamic injection.
     *
     * @param source  the method to be injected
     * @param into    the method to be injected to
     * @param at      the point where to inject
     * @param ordinal the ordinal, see {@link Inject#ordinal()}
     * @see Inject#ordinal()
     */
    void inject(Method source, Method into, InjectionPoint at, int ordinal);
}
