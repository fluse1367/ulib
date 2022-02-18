package eu.software4you.ulib.core.api.reflect;

import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.core.api.util.value.Unsettled;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

/**
 * Useful shortcuts for the Java Reflection API.<br>
 */
public abstract class ReflectUtil {

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @SneakyThrows
    @Deprecated
    public static Object callGuess(String clazz, Object invoker, String call, Object... parameters) {
        return callGuess(Class.forName(clazz, true, getCallerClass().getClassLoader()),
                invoker, call, parameters);
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
        return forceCallGuess(Class.forName(clazz, true, getCallerClass().getClassLoader()),
                invoker, call, parameters);
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
        return call(Class.forName(clazz, true, getCallerClass().getClassLoader()),
                invoker, call, parameters);
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
     * @param invoker    the object the call is invoked from, if {@code null} the call will begin from a static field or method
     * @param call       the call itself
     * @param forced     if private elements should be called and fields with the {@link Modifier#FINAL} modifier should be set anyway
     * @param parameters the parameters used for the methods to call, or for a field to set;
     *                   see {@link Parameter}, {@link Parameter#guess(Object)}, and {@link Parameter#guessMultiple(Object[])}
     * @return the value the last method or field returns, or the value that a field is set to by the call
     */
    @SafeVarargs
    public static Object call(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>... parameters) {
        return Providers.get(ReflectUtil.class).call0(clazz, invoker, call, forced, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SneakyThrows
    @SafeVarargs
    public static Object forceCall(String clazz, Object invoker, String call, List<Parameter<?>>... parameters) {
        return forceCall(Class.forName(clazz, true, getCallerClass().getClassLoader()),
                invoker, call, parameters);
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
        return Providers.get(ReflectUtil.class).getCallerClass0(++depth);
    }

    protected abstract Object call0(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>[] parameters);

    protected abstract Class<?> getCallerClass0(int depth);

    /**
     * Tries to load a certain class.
     *
     * @param name the fully qualified name of the class
     * @param init if the class should be initialized in case of loading success
     * @return a {@link Unsettled} object wrapping the operation result
     */
    @NotNull
    public static Unsettled<Class<?>> forName(@NotNull String name, boolean init) {
        return forName(name, init, getCallerClass().getClassLoader());
    }

    /**
     * Tries to load a certain class.
     *
     * @param name   the fully qualified name of the class
     * @param init   if the class should be initialized in case of loading success
     * @param loader the loader from which the class is loaded
     * @return a {@link Unsettled} object wrapping the operation result
     */
    @NotNull
    public static Unsettled<Class<?>> forName(@NotNull String name, boolean init, ClassLoader loader) {
        return Unsettled.execute(new Supplier<Class<?>>() {
            @SneakyThrows
            @Override
            public Class<?> get() {
                return Class.forName(name, init, loader);
            }
        });
    }

    /**
     * Tries to obtain a value of an enum.
     *
     * @param enumClass the class of enum
     * @param enumEntry the fully qualified name of the desired entry.
     * @return an optional wrapping the enum entry value on success, an empty optional otherwise
     */
    @NotNull
    public static <E extends Enum<?>> Optional<E> getEnumEntry(@NotNull Class<E> enumClass, @NotNull String enumEntry) {
        return Unsettled.execute(new Supplier<E>() {
            @SneakyThrows
            @Override
            public E get() {
                //noinspection unchecked
                return (E) enumClass.getMethod("valueOf", String.class).invoke(null, enumEntry);
            }
        }).get();
    }

    /**
     * Searches a class and it's superclasses for a certain field.
     *
     * @param clazz     the class to search in
     * @param fieldName the name of the field to find
     * @param declared  if the non-public fields should be searched as well
     * @return an optional wrapping the field, or an empty optional if the field cannot be found
     */
    @NotNull
    public static Optional<Field> findUnderlyingField(@NotNull Class<?> clazz, @NotNull String fieldName, boolean declared) {
        Class<?> current = clazz;
        do {
            try {
                return Optional.of(declared ? current.getDeclaredField(fieldName) : current.getField(fieldName));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return Optional.empty();
    }

    /**
     * Collects all fields from a certain class and it's superclasses.
     *
     * @param clazz the class to search in
     * @return The collection of fields
     */
    @NotNull
    public static Collection<Field> findUnderlyingFields(@NotNull Class<?> clazz, boolean declared) {
        List<Field> cache = new ArrayList<>();
        Class<?> current = clazz;
        do {
            try {
                cache.addAll(Arrays.asList(declared ? current.getDeclaredFields() : current.getFields()));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return cache;
    }

    /**
     * Searches a class and it's superclasses for a certain method.
     *
     * @param clazz      the class to search in
     * @param methodName the name of the method to find
     * @param declared   if the non-public methods should be searched as well
     * @return an optional wrapping the method, or an empty optional if the method cannot be found
     */
    @NotNull
    public static Optional<Method> findUnderlyingMethod(@NotNull Class<?> clazz, @NotNull String methodName, boolean declared, @NotNull Class<?>... parameterTypes) {
        Class<?> current = clazz;
        do {
            try {
                return Optional.of(declared ? current.getDeclaredMethod(methodName, parameterTypes)
                        : current.getMethod(methodName, parameterTypes));
            } catch (Exception ignored) {
            }
        } while ((current = current.getSuperclass()) != null);
        return Optional.empty();
    }
}