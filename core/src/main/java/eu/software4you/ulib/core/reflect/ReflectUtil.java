package eu.software4you.ulib.core.reflect;

import eu.software4you.ulib.core.impl.reflect.ReflectSupport;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.StackWalker.StackFrame;
import java.lang.reflect.*;
import java.util.*;

public class ReflectUtil {

    /**
     * Calls a chain of methods/fields from an initial entry point.
     * <p>
     * The call string is a chain of method/field names separated by a dot, methods are indicated by brackets :
     * <pre>{@code someMethod().someField.someMethod()}</pre>
     *
     * @param entry  the initial entry point
     * @param invoke the invoking object of the entry
     * @param call   the call string
     * @param params the parameters according to the call string
     * @param <R>    return type
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static <R> Expect<R, ReflectiveOperationException> call(@NotNull Class<?> entry, @Nullable Object invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(entry, invoke, ReflectSupport.buildFramePath(call, params));
    }

    /**
     * Calls a chain of methods/fields from an initial entry point.
     * If a {@link CallFrame} representing a field has at least one parameter, the field will be set to this parameter before the execution continues.
     *
     * @param entry  the initial entry point
     * @param invoke the invoking object of the entry
     * @param path   the calling path
     * @param <R>    return type
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static <R> Expect<R, ReflectiveOperationException> call(@NotNull Class<?> entry, @Nullable Object invoke, @NotNull CallFrame... path) {
        return Expect.compute(() -> {

            Class<?> clazz = entry;
            Object instance = invoke;

            for (CallFrame frame : path) {

                var res = ReflectSupport.frameCall(frame, clazz, instance);
                clazz = res.getFirst(); // return type
                instance = res.getSecond(); // returned object

            }

            return (R) instance;
        });
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
     */
    public static Class<?> getCallerClass(int depth) {
        return getCaller(depth + 1).getDeclaringClass();
    }

    /**
     * Determines the currently calling {@link StackFrame frame} of the current stack trace
     *
     * @return the calling {@link StackFrame frame}
     */
    public static StackFrame getCaller() {
        return getCaller(2);
    }

    /**
     * Determines the n-th calling {@link StackFrame frame} of the current stack trace, where 1 is the current calling {@link StackFrame frame}
     *
     * @param depth the index of the calling stack trace element
     * @return the calling {@link StackFrame frame}
     */
    public static StackFrame getCaller(int depth) {
        var stack = getCallerStack();
        Objects.checkIndex(depth, stack.length);
        return stack[depth];
    }

    /**
     * Builds the calling stack
     *
     * @return the stack array
     */
    public static StackFrame[] getCallerStack() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(st -> st.skip(2) // skip this
                        .toArray(StackFrame[]::new));
    }

    /**
     * Attempts to load a certain class.
     *
     * @param name the fully qualified name of the class
     * @param init if the class should be initialized in case of loading success
     * @return a {@link Expect} object wrapping the operation result
     */
    @NotNull
    public static Expect<Class<?>, ClassNotFoundException> forName(@NotNull String name, boolean init) {
        return forName(name, init, getCallerClass().getClassLoader());
    }

    /**
     * Attempts to load a certain class.
     *
     * @param name   the fully qualified name of the class
     * @param init   if the class should be initialized in case of loading success
     * @param loader the loader from which the class is loaded
     * @return a {@link Expect} object wrapping the operation result
     */
    @NotNull
    public static Expect<Class<?>, ClassNotFoundException> forName(@NotNull String name, boolean init, ClassLoader loader) {
        return Expect.compute(Class::forName, name, init, loader);
    }

    /**
     * Attempts to obtain a value of an enum.
     *
     * @param enumClass the class of enum
     * @param enumEntry the fully qualified name of the desired entry.
     * @return a {@link Expect} object wrapping the operation result
     */
    @NotNull
    public static <E extends Enum<?>> Expect<E, IllegalArgumentException> getEnumEntry(@NotNull Class<E> enumClass, @NotNull String enumEntry) {
        //noinspection unchecked
        var res = Expect.compute(() -> enumClass.getMethod("valueOf", String.class)
                        .invoke(null, enumEntry))
                .<E, ReflectiveOperationException>map(o -> (E) o);

        if (res.isPresent())
            return res.toOther();

        if (res.getCaught().orElseThrow() instanceof InvocationTargetException ex)
            return Expect.failed((IllegalArgumentException) ex.getTargetException());

        // should never happen
        throw new IllegalStateException();
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
