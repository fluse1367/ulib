package eu.software4you.ulib.core.reflect;

import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.impl.reflect.ReflectSupport;
import eu.software4you.ulib.core.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.StackWalker.StackFrame;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Stream;

public class ReflectUtil {

    /**
     * Calls a chain of methods/fields from an initial entry point object ("instance-call").
     * <p>
     * The call string is a chain of method/field names separated by a dot, methods are indicated by brackets :
     * <pre>{@code someMethod().someField.someMethod()}</pre>
     *
     * @param invoke the invoking object of the entry
     * @param call   the call string
     * @param params the parameters according to the call string
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static Expect<Object, ReflectiveOperationException> icall(@NotNull Object invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(invoke.getClass(), invoke, call, params);
    }

    /**
     * Calls a chain of methods/fields from an initial entry point object ("instance-call").
     * <p>
     * The call string is a chain of method/field names separated by a dot, methods are indicated by brackets :
     * <pre>{@code someMethod().someField.someMethod()}</pre>
     *
     * @param returnType return type
     * @param invoke     the invoking object of the entry
     * @param call       the call string
     * @param params     the parameters according to the call string
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static <R> Expect<R, ReflectiveOperationException> icall(@NotNull Class<R> returnType, @NotNull Object invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(returnType, invoke.getClass(), invoke, call, params);
    }

    /**
     * Calls a chain of methods/fields from an initial static entry point ("static-call").
     * <p>
     * The call string is a chain of method/field names separated by a dot, methods are indicated by brackets :
     * <pre>{@code someMethod().someField.someMethod()}</pre>
     *
     * @param invoke the initial entry point
     * @param call   the call string
     * @param params the parameters according to the call string
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static Expect<Object, ReflectiveOperationException> scall(@NotNull Class<?> invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(invoke, null, call, params);
    }

    /**
     * Calls a chain of methods/fields from an initial static entry point ("static-call").
     * <p>
     * The call string is a chain of method/field names separated by a dot, methods are indicated by brackets :
     * <pre>{@code someMethod().someField.someMethod()}</pre>
     *
     * @param returnType return type
     * @param invoke     the initial entry point
     * @param call       the call string
     * @param params     the parameters according to the call string
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static <R> Expect<R, ReflectiveOperationException> scall(@NotNull Class<R> returnType, @NotNull Class<?> invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(returnType, invoke, null, call, params);
    }

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
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static Expect<Object, ReflectiveOperationException> call(@NotNull Class<?> entry, @Nullable Object invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(entry, invoke, ReflectSupport.buildFramePath(call, params));
    }

    /**
     * Calls a chain of methods/fields from an initial entry point.
     * <p>
     * The call string is a chain of method/field names separated by a dot, methods are indicated by brackets :
     * <pre>{@code someMethod().someField.someMethod()}</pre>
     *
     * @param returnType return type
     * @param entry      the initial entry point
     * @param invoke     the invoking object of the entry
     * @param call       the call string
     * @param params     the parameters according to the call string
     * @return the execution result
     * @see #call(Class, Object, CallFrame...)
     */
    @SafeVarargs
    public static <R> Expect<R, ReflectiveOperationException> call(@NotNull Class<R> returnType, @NotNull Class<?> entry, @Nullable Object invoke, @NotNull String call, @Nullable List<Param<?>>... params) {
        return call(returnType, entry, invoke, ReflectSupport.buildFramePath(call, params));
    }

    /**
     * Calls a chain of methods/fields from an initial entry point.
     * If a {@link CallFrame} representing a field has at least one parameter, the field will be set to this parameter before the execution continues.
     *
     * @param entry  the initial entry point
     * @param invoke the invoking object of the entry
     * @param path   the calling path
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static Expect<Object, ReflectiveOperationException> call(@NotNull Class<?> entry, @Nullable Object invoke, @NotNull CallFrame... path) {
        return call(Object.class, entry, invoke, path);
    }

    /**
     * Calls a chain of methods/fields from an initial entry point.
     * If a {@link CallFrame} representing a field has at least one parameter, the field will be set to this parameter before the execution continues.
     *
     * @param returnType the return type
     * @param entry      the initial entry point
     * @param invoke     the invoking object of the entry
     * @param path       the calling path
     * @return an expect object wrapping the execution result
     */
    @NotNull
    public static <R> Expect<R, ReflectiveOperationException> call(@NotNull Class<R> returnType, @NotNull Class<?> entry, @Nullable Object invoke, @NotNull CallFrame... path) {
        return Expect.compute(() -> {

            Class<?> clazz = entry;
            Object instance = invoke;

            for (CallFrame frame : path) {

                var res = ReflectSupport.frameCall(frame, clazz, instance);
                clazz = res.getFirst(); // return type
                instance = res.getSecond(); // returned object

            }

            return returnType.cast(instance);
        });
    }

    /**
     * Creates a new instance of the given annotation type.
     *
     * @param type    the type to create an instance of
     * @param members the member -> value map
     * @return the new annotation instance
     * @throws IncompleteAnnotationException If the member map is missing a required member
     */
    @NotNull
    public static <T extends Annotation> T instantiateAnnotation(@NotNull Class<T> type, @NotNull Map<String, ?> members)
            throws IncompleteAnnotationException {

        var methods = type.getDeclaredMethods();
        Map<String, Object> annoValues = new HashMap<>(methods.length, 1f);

        // validate & acquire members
        for (Method method : methods) {
            var name = method.getName();

            var value = Expect.ofNullable((Object) members.get(name))
                    .orElseGet(method::getDefaultValue)
                    .orElseThrow(() -> new IncompleteAnnotationException(type, name));

            annoValues.put(name, value);
        }

        var cl = forName("sun.reflect.annotation.AnnotationParser", true, ClassLoader.getSystemClassLoader())
                .orElseThrow();

        return scall(Annotation.class, cl, "annotationForMap()",
                Param.listOf(Class.class, type, Map.class, annoValues))
                .map(type::cast)
                .orElseThrow();
    }

    /**
     * Determines if a particular (ulib) class is considered hidden in a call stack.
     * <p>
     * A class is considered hidden if it is a utility call class (such as {@link Expect}),
     * a reflection utility class such as {@link ReflectUtil} or if it is an implementation specific class.
     * <p>
     * This method will return {@code false} for any non-ulib class.
     *
     * @param clazz the ulib class to check
     * @return {@code true} if the class is considered hidden in call stack
     */
    public static boolean isHidden(@NotNull Class<?> clazz) {
        if (!Internal.isUlibClass(clazz))
            return false;

        if (Internal.isImplClass(clazz))
            return true;

        return  // reflection
                clazz == ReflectUtil.class

                // utility
                || clazz == Conditions.class
                || clazz == Conversions.class
                || clazz == Expect.class
                || clazz == LazyValue.class
                ;
    }

    /**
     * Attempts to identify an (infinite) recursion by checking for a recurring pattern in the current stack.
     *
     * @param threshold           the minimum number a pattern has to occur in the current stack to be considered an (infinite) recursion
     * @param maxPatternLength    the maximum length a pattern may have
     * @param ignoreLeadingFrames the number of leading elements in the stack that should be ignored (to compensate branching);
     *                            E.g. if this method is called directly inside the critical part of another code this may be {@code 0},
     *                            if this method however is called inside a helper method this may be {@code 1} or more.
     * @return {@code true}, if the current stack can be considered an (infinite) recursion
     * @deprecated The current implementation is experimental, unsafe, doesn't work properly and is likely to get removed.
     */
    @Deprecated(forRemoval = true)
    public static boolean identifyRecursion(int threshold, int maxPatternLength, int ignoreLeadingFrames) {
        // check args
        if (threshold <= 0)
            throw new IllegalArgumentException("Threshold cannot be a negative number");
        if (maxPatternLength <= 0)
            throw new IllegalArgumentException("Max pattern length cannot be a negative number");
        if (ignoreLeadingFrames < 0)
            throw new IllegalArgumentException("Cannot ignore negative number of leading frames");

        // redirect to impl
        return ReflectSupport.identifyRecursion(threshold, maxPatternLength, ignoreLeadingFrames + 1);
    }

    /**
     * Determines the currently calling {@link Class} from the perspective of the caller of this method.
     * <p>
     * This method is equal to directly calling {@link #getCallerClass(int) getCallerClass(1)}.
     *
     * @return the calling {@link Class}
     * @see #getCallerClass(int)
     */
    public static Class<?> getCallerClass() {
        return getCallerClass(2);
    }

    /**
     * Determines the n-th calling {@link Class} of the current stack trace,
     * where 1 is the currently calling {@link Class} from the perspective of the caller of this method.
     *
     * @param depth the index of the calling stack trace element, where 1 is the currently calling {@link Class}
     *              and 0 the {@link Class} this method is being called from
     * @return the calling {@link Class}
     */
    public static Class<?> getCallerClass(int depth) {
        return getCaller(depth + 1).getDeclaringClass();
    }

    /**
     * Determines the currently calling {@link StackFrame frame} from the perspective of the caller of this method.
     * <p>
     * This method is equal to directly calling {@link #getCaller(int) getCaller(1)}.
     *
     * @return the calling {@link StackFrame frame}
     */
    public static StackFrame getCaller() {
        return getCaller(2);
    }

    /**
     * Determines the n-th calling {@link StackFrame frame} of the current stack trace,
     * where 1 is the currently calling {@link StackFrame frame} from the perspective of the caller of this method.
     *
     * @param depth the index of the calling stack trace element
     * @return the calling {@link StackFrame frame}
     */
    public static StackFrame getCaller(int depth) {
        return getCallerStackAsStream()
                .skip(depth)
                .findFirst()
                .orElseThrow();
    }

    /**
     * Builds the calling stack, where the first element is the currently calling {@link StackFrame frame}
     * from the perspective of the caller of this method.
     *
     * @return the stack array
     */
    public static StackFrame[] getCallerStack() {
        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(st -> st.skip(2) // skip this
                        .toArray(StackFrame[]::new));
    }

    /**
     * Builds the calling stack, where the first element is the currently calling {@link StackFrame frame}
     * from the perspective of the caller of this method.
     *
     * @return the stack array
     */
    public static Stream<StackFrame> getCallerStackAsStream() {
        return Arrays.stream(getCallerStack())
                .skip(1) // skip this
                ;
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
            //noinspection unchecked,rawtypes
            return (Expect) res;

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

    /**
     * Automatically checks two objects for equality.
     * <p>
     * The subsequent implementation does not use {@link Object#equals(Object)} on the given objects.
     * Instead, the hash values or the underlying fields itself are compared against each other.
     *
     * @param someObject  the first object
     * @param otherObject the second object
     * @return {@code true} if the given objects are considered equal, {@code false} otherwise
     * @see #autoHash(Object)
     */
    public static boolean autoEquals(@Nullable Object someObject, @Nullable Object otherObject) {
        if (someObject == otherObject)
            return true; // same instance

        if (someObject == null || otherObject == null
            || someObject.getClass() != otherObject.getClass())
            return false;

        // try to "prove" equality by hash codes
        if (autoHash(someObject) == autoHash(otherObject))
            return true;

        // check all fields
        return ReflectSupport.deepEquals(someObject, otherObject);
    }

    /**
     * Automatically computes a hash code of the given object.
     * <p>
     * The Subsequent implementation does not depend on {@link Object#hashCode()}, however it will use it in case it is implemented.
     * In <b>no case</b> the {@link System#identityHashCode(Object) identity hash code} is used.
     * <p>
     * If {@link Object#hashCode()} is not implemented, a hash code is automatically computed from all fields
     * (including fields from super classes; excluding static, synthetic and transient fields) of the given object.
     *
     * @param obj the object of whose to compute the hash
     * @return a hash code
     */
    public static int autoHash(@Nullable Object obj) {
        if (obj == null)
            return 0;

        var caller = getCaller();
        boolean isImpl = caller.getMethodName().equals("hashCode") && caller.getMethodType().parameterCount() == 0;

        return ReflectSupport.deepHash(obj, obj.getClass(), !isImpl);
    }

}
