package eu.software4you.reflect;

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


}
