package eu.software4you.reflection;

import eu.software4you.ulib.ULib;
import eu.software4you.utils.ClassUtils;
import eu.software4you.utils.FileUtils;
import org.apache.commons.lang.ArrayUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * The {@code ReflectUtil} class provides useful shortcuts for the Java Reflection API.<br>
 * An example using the regular Java Reflection API:<br>
 * <code>
 * Method m = Class.forName("a.Clazz").getMethod("aMethod", Class.forName("another.Clazz"));
 * m.setAccessible(true);
 * Object obj = m.invoke(null, anotherClazzInstance);
 * Field f = obj.getClass().getField("aField");
 * f.setAccessible(true);
 * Object field = f.get(obj);
 * </code>
 * <p>
 * With this {@code ReflectUtil} API:
 * <code>Object field = ReflectUtil.forceCall("a.Clazz", null, "aMethod().aField", Parameter.guessSingle(anotherClazzInstance));</code>
 *
 * @author fluse1367 (software4you.eu)
 * @version 1.3
 * @apiNote <b>This class is experimental and may be unstable or causing unexpected behaviour!</b>
 */
public class ReflectUtil {
    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @Deprecated
    public static Object callGuess(String clazz, Object invoker, String call, Object... parameters)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        return callGuess(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @Deprecated
    public static Object callGuess(Class<?> clazz, Object invoker, String call, Object... parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        return call(clazz, invoker, call, false, Parameter.guessMultiple(parameters));
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @Deprecated
    public static Object forceCallGuess(String clazz, Object invoker, String call, Object... parameters)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        return forceCallGuess(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     * @deprecated the parameter classes (that are needed to determine a method using reflection) are guessed
     */
    @Deprecated
    public static Object forceCallGuess(Class<?> clazz, Object invoker, String call, Object... parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        return call(clazz, invoker, call, true, Parameter.guessMultiple(parameters));
    }


    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SafeVarargs
    public static Object call(String clazz, Object invoker, String call, List<Parameter<?>>... parameters)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        return call(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SafeVarargs
    public static Object call(Class<?> clazz, Object invoker, String call, List<Parameter<?>>... parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
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
     * @throws NoSuchMethodException     if a matching method is not found for the current call element or if the name is "&lt;init&gt;"or "&lt;clinit&gt;".
     * @throws InvocationTargetException if the current underlying method throws an exception.
     * @throws IllegalAccessException    if the current {@code Method} object is enforcing Java language access control and the underlying method is inaccessible.
     * @throws NoSuchFieldException      if a field specified in the call is {@code null}
     */
    // main call method
    @SafeVarargs
    public static Object call(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>... parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        String[] callParts = call.split(Pattern.quote("."));

        File utilsFile = FileUtils.getClassFile(ULib.class);
        // depth 0, bc calls in this class will be completely ignored; so the next not ignored call comes from the actual caller
        File callerFile = FileUtils.getClassFile(getCallerClass(0));
        if (utilsFile != null && utilsFile.equals(callerFile)) // internal ReflectUtil call, allowing this!
            utilsFile = null;

        for (int i = 0; i < callParts.length; i++) {

            // security step to ensure nobody tries to change/access parts of utils they shouldn't change/access
            File classFile = FileUtils.getClassFile(clazz);
            if (classFile != null && classFile.equals(utilsFile))
                throw new UnsupportedOperationException("Nope. You won't access this :)");

            Object returned;
            Class<?> returnType;

            Object[] arguments = new Object[0];
            Class<?>[] argumentTypes = new Class[0];

            if (parameters.length > i && parameters[i] != null && !parameters[i].isEmpty()) {
                List<Object> argumentsAsList = new ArrayList<>();
                List<Class<?>> argumentTypesAsList = new ArrayList<>();

                List<Parameter<?>> params = parameters[i];
                params.forEach(p -> {
                    argumentsAsList.add(p.get());
                    argumentTypesAsList.add(p.what());
                });

                arguments = argumentsAsList.toArray(arguments);
                argumentTypes = argumentTypesAsList.toArray(argumentTypes);
            }


            if (callParts[i].endsWith("()")) {
                callParts[i] = callParts[i].substring(0, callParts[i].length() - 2);
                // we should call a method

                Method method = (forced ? ClassUtils.findUnderlyingDeclaredMethod(clazz, callParts[i], argumentTypes)
                        : ClassUtils.findUnderlyingMethod(clazz, callParts[i], argumentTypes));
                if (method == null)
                    throw new NoSuchMethodException(String.format("%s(%s) in %s (%sforced)", callParts[i], ArrayUtils.toString(argumentTypes), clazz.toString(), forced ? "" : "not "));
                if (forced)
                    method.setAccessible(true);
                returned = method.invoke(invoker, arguments);
                returnType = method.getReturnType();
            } else {
                // the call is a field
                Field field = (forced ?
                        ClassUtils.findUnderlyingDeclaredField(clazz, callParts[i])
                        : ClassUtils.findUnderlyingField(clazz, callParts[i]));
                if (field == null)
                    throw new NoSuchFieldException(String.format("%s in %s (%sforced)", callParts[i], clazz.toString(), forced ? "" : "not "));
                if (forced)
                    field.setAccessible(true);
                if (arguments.length > 0) {
                    Object value = arguments[0];

                    if (forced && Modifier.isFinal(field.getModifiers())) {
                        Field modifiersField = Field.class.getDeclaredField("modifiers");
                        modifiersField.setAccessible(true);
                        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
                    }
                    field.set(invoker, value);
                    return value;
                }
                returned = field.get(invoker);
                returnType = field.getType();
            }

            if (!(i < callParts.length - 1)) {
                return returned;
            }
            clazz = returnType;
            invoker = returned;
        }
        return null;

    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SafeVarargs
    public static Object forceCall(String clazz, Object invoker, String call, List<Parameter<?>>... parameters)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, NoSuchFieldException {
        return forceCall(Class.forName(clazz), invoker, call, parameters);
    }

    /**
     * @see #call(Class, Object, String, boolean, List[])
     */
    @SafeVarargs
    public static Object forceCall(Class<?> clazz, Object invoker, String call, List<Parameter<?>>... parameters)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        return call(clazz, invoker, call, true, parameters);
    }


    /**
     * Determines the {@link Class} the current method is being called from
     *
     * @return the calling {@link Class}
     * @see #getCallerClass(int)
     */
    public static Class<?> getCallerClass() {
        return getCallerClass(1);
    }

    /**
     * Determines the {@link Class} name the current method is being called from
     *
     * @return the calling {@link Class} name
     * @see #getCallerClassName(int)
     */
    public static String getCallerClassName() {
        return getCallerClassName(1);
    }

    /**
     * Determines the n-th calling {@link Class} name of the current stack trace
     *
     * @param depth the index of the calling stack trace element, where 1 is the current calling {@link Class} name
     *              and 0 the {@link Class} name of the class this method is being called from
     * @return the calling {@link Class} name
     * @throws IllegalArgumentException if {@code depth} is negative or invalid
     */
    public static String getCallerClassName(int depth) {
        if (depth < 0)
            throw new IllegalArgumentException("Negative depth");
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        int j = 0;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ReflectUtil.class.getName()) && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (j >= depth)
                    return ste.getClassName();
                j++;
            }
        }
        throw new IllegalArgumentException("Invalid depth");
    }

    /**
     * Determines the n-th calling {@link Class} of the current stack trace, where 1 is the current calling {@link Class}
     *
     * @param depth the index of the calling stack trace element, where 1 is the current calling {@link Class}
     *              and 0 the {@link Class} this method is being called from
     * @return the calling {@link Class}
     * @throws IllegalArgumentException if {@code depth is negative} or invalid
     * @throws Error                    if {@link Class} name of the n-th calling class could not be resolved. <b>This should NEVER happen.</b>
     */
    public static Class<?> getCallerClass(int depth) {
        try {
            return Class.forName(getCallerClassName(depth));
        } catch (ClassNotFoundException e) {
            throw new Error("Critical JVM Failure!", e);
        }
    }

    /**
     * Represents an enumeration of Java data types with corresponding classes
     * <p>
     * This class is part of the <b>BukkitReflectionUtils</b> and follows the same usage conditions
     *
     * @author DarkBlade12
     * @since 1.0
     */
    public enum DataType {
        BYTE(byte.class, Byte.class),
        SHORT(short.class, Short.class),
        INTEGER(int.class, Integer.class),
        LONG(long.class, Long.class),
        CHARACTER(char.class, Character.class),
        FLOAT(float.class, Float.class),
        DOUBLE(double.class, Double.class),
        BOOLEAN(boolean.class, Boolean.class);

        private static final Map<Class<?>, DataType> CLASS_MAP = new HashMap<Class<?>, DataType>();

        // Initialize map for quick class lookup
        static {
            for (DataType type : values()) {
                CLASS_MAP.put(type.primitive, type);
                CLASS_MAP.put(type.reference, type);
            }
        }

        private final Class<?> primitive;
        private final Class<?> reference;

        /**
         * Construct a new data type
         *
         * @param primitive Primitive class of this data type
         * @param reference Reference class of this data type
         */
        DataType(Class<?> primitive, Class<?> reference) {
            this.primitive = primitive;
            this.reference = reference;
        }

        /**
         * Returns the data type with the given primitive/reference class
         *
         * @param clazz Primitive/Reference class of the data type
         * @return The data type
         */
        public static DataType fromClass(Class<?> clazz) {
            return CLASS_MAP.get(clazz);
        }

        /**
         * Returns the primitive class of the data type with the given reference class
         *
         * @param clazz Reference class of the data type
         * @return The primitive class
         */
        public static Class<?> getPrimitive(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getPrimitive();
        }

        /**
         * Returns the reference class of the data type with the given primitive class
         *
         * @param clazz Primitive class of the data type
         * @return The reference class
         */
        public static Class<?> getReference(Class<?> clazz) {
            DataType type = fromClass(clazz);
            return type == null ? clazz : type.getReference();
        }

        /**
         * Returns the primitive class array of the given class array
         *
         * @param classes Given class array
         * @return The primitive class array
         */
        public static Class<?>[] getPrimitive(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getPrimitive(classes[index]);
            }
            return types;
        }

        /**
         * Returns the reference class array of the given class array
         *
         * @param classes Given class array
         * @return The reference class array
         */
        public static Class<?>[] getReference(Class<?>[] classes) {
            int length = classes == null ? 0 : classes.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getReference(classes[index]);
            }
            return types;
        }

        /**
         * Returns the primitive class array of the given object array
         *
         * @param objects Given object array
         * @return The primitive class array
         */
        public static Class<?>[] getPrimitive(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getPrimitive(objects[index].getClass());
            }
            return types;
        }

        /**
         * Returns the reference class array of the given object array
         *
         * @param objects Given object array
         * @return The reference class array
         */
        public static Class<?>[] getReference(Object[] objects) {
            int length = objects == null ? 0 : objects.length;
            Class<?>[] types = new Class<?>[length];
            for (int index = 0; index < length; index++) {
                types[index] = getReference(objects[index].getClass());
            }
            return types;
        }

        /**
         * Compares two class arrays on equivalence
         *
         * @param primary   Primary class array
         * @param secondary Class array which is compared to the primary array
         * @return Whether these arrays are equal or not
         */
        public static boolean compare(Class<?>[] primary, Class<?>[] secondary) {
            if (primary == null || secondary == null || primary.length != secondary.length) {
                return false;
            }
            for (int index = 0; index < primary.length; index++) {
                Class<?> primaryClass = primary[index];
                Class<?> secondaryClass = secondary[index];
                if (primaryClass.equals(secondaryClass) || primaryClass.isAssignableFrom(secondaryClass)) {
                    continue;
                }
                return false;
            }
            return true;
        }

        /**
         * Returns the primitive class of this data type
         *
         * @return The primitive class
         */
        public Class<?> getPrimitive() {
            return primitive;
        }

        /**
         * Returns the reference class of this data type
         *
         * @return The reference class
         */
        public Class<?> getReference() {
            return reference;
        }
    }


    /**
     * A wrapper class for any values, that holds class and an assignable value.
     * Used to unique determine methods in reflection.
     *
     * @param <C> the class
     * @see #call(Class, Object, String, boolean, List[])
     */
    public static class Parameter<C> {

        private final Class<C> clazz;
        private C value;

        /**
         * @param clazz a class
         * @param value an assignable value to the class
         */
        public Parameter(Class<C> clazz, C value) {
            this.clazz = clazz;
            this.value = value;
        }

        /**
         * Constructs a new instance with the direct class of any value
         *
         * @param value the value
         * @param <C>   the class
         * @return the instance
         * @see #guess(Object)
         */
        public static <C> Parameter<C> from(C value) {
            return new Parameter<>((Class<C>) DataType.getPrimitive(value.getClass()), value);
        }

        /**
         * Constructs a new instance with the direct class of any value
         *
         * @param value the value
         * @param <C>   the class
         * @return the instance
         * @see #from(Object)
         */
        public static <C> Parameter<?> guess(C value) {
            return from(value);
        }

        /**
         * Constructs a list of new instances with the direct class of any value
         *
         * @param values the values
         * @param <C>    the class
         * @return a list of new instances
         * @see #from(Object)
         */
        @SafeVarargs
        public static <C> List<Parameter<C>> fromMultiple(C... values) {
            List<Parameter<C>> list = new ArrayList<>();
            for (C value : values) {
                list.add(from(value));
            }
            return list;
        }

        /**
         * Constructs a list of new instances with the direct class of any value
         *
         * @param values the values
         * @param <C>    the class
         * @return a list of new instances
         * @see #guess(Object)
         */
        @SafeVarargs
        public static <C> List<Parameter<?>> guessMultiple(C... values) {
            List<Parameter<?>> list = new ArrayList<>();
            for (C value : values) {
                list.add(guess(value));
            }
            return list;
        }

        /**
         * @return the class
         */
        public Class<C> what() {
            return clazz;
        }

        /**
         * @return the value
         */
        public C get() {
            return value;
        }

        /**
         * Sets a new value
         *
         * @param value the value to set
         */
        public void set(C value) {
            this.value = value;
        }

    }
}
