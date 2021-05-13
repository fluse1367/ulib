package eu.software4you.reflect;

import eu.software4you.ulib.Await;
import lombok.SneakyThrows;

import java.lang.reflect.Modifier;
import java.util.List;

/**
 * Useful shortcuts for the Java Reflection API.<br>
 */
public abstract class ReflectUtil {
    @Await
    private static ReflectUtil impl;

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @SneakyThrows
    @Deprecated
    public static Object callGuess(String clazz, Object invoker, String call, Object... parameters) {
        return callGuess(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @Deprecated
    public static Object callGuess(Class<?> clazz, Object invoker, String call, Object... parameters) {
        return call(clazz, invoker, call, false, Parameter.guessMultiple(parameters));
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @SneakyThrows
    @Deprecated
    public static Object forceCallGuess(String clazz, Object invoker, String call, Object... parameters) {
        return forceCallGuess(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @Deprecated
    public static Object forceCallGuess(Class<?> clazz, Object invoker, String call, Object... parameters) {
        return call(clazz, invoker, call, true, Parameter.guessMultiple(parameters));
    }


    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SneakyThrows
    @SafeVarargs
    public static Object call(String clazz, Object invoker, String call, List<Parameter<?>>... parameters) {
        return call(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SafeVarargs
    public static Object call(Class<?> clazz, Object invoker, String call, List<Parameter<?>>... parameters) {
        return call(clazz, invoker, call, false, parameters);
    }

    /**
     * Calls a chain of methods or fields from the {@code clazz} as root.
     * Syntax is as following: {@code method().field.method().field}
     * The given {@code parameters} will be used to call a method.
     * If a parameter is given for where a field is in the call, the field will be set to the value of the parameter.
     * In this case this method will return the value the field was set to.
     *
     * @param clazz      the class the call should go from
     * @param invoker    the object the call is invoked from, if null the call will begin from a static field or method
     * @param call       the call itself
     * @param forced     if private elements should be called and fields with the {@link Modifier#FINAL} modifier should be set anyway
     * @param parameters the parameters used for the methods to call, or for a field to set;
     *                   see {@link Parameter}, {@link Parameter#guess(Object)}, and {@link Parameter#guessMultiple(Object[])}
     * @return the value the last method or field returns, or the value that a field is set to by the call
     */
    @SafeVarargs
    public static Object call(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>... parameters) {
        return impl.call0(clazz, invoker, call, forced, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SneakyThrows
    @SafeVarargs
    public static Object forceCall(String clazz, Object invoker, String call, List<Parameter<?>>... parameters) {
        return forceCall(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SafeVarargs
    public static Object forceCall(Class<?> clazz, Object invoker, String call, List<Parameter<?>>... parameters) {
        return call(clazz, invoker, call, true, parameters);
    }

    /**
     * Determines the {@link Class} the current method is being called from
     *
     * @return the calling {@link Class}
     * @see #getCallerClass(int)
     */
    public static Class<?> getCallerClass() {
        return getCallerClass(2);
    }


    /**
     * Determines the n-th calling {@link Class} of the current stack trace, where 1 is the current calling {@link Class}
     *
     * @param depth the index of the calling stack trace element, where 1 is the current calling {@link Class}
     *              and 0 the {@link Class} this method is being called from
     * @return the calling {@link Class}
     * @throws IllegalArgumentException if {@code depth} is invalid
     */
    public static Class<?> getCallerClass(int depth) {
        return impl.getCallerClass0(++depth);
    }

    protected abstract Object call0(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>[] parameters);

    protected abstract Class<?> getCallerClass0(int depth);
}
