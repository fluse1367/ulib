package eu.software4you.ulib.impl.reflect;

import eu.software4you.reflect.Parameter;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.utils.ClassUtils;
import lombok.SneakyThrows;
import lombok.var;
import org.apache.commons.lang.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Impl(ReflectUtil.class)
public final class ReflectUtilImpl extends ReflectUtil {

    public static String retrieveCallerClassName(int depth) {
        if (depth < 0)
            throw new IllegalArgumentException("Negative depth");
        StackTraceElement[] stElements = Thread.currentThread().getStackTrace();
        int j = 0;
        for (int i = 1; i < stElements.length; i++) {
            StackTraceElement ste = stElements[i];
            if (!ste.getClassName().equals(ReflectUtil.class.getName())
                    && !ste.getClassName().equals(ReflectUtilImpl.class.getName())
                    && ste.getClassName().indexOf("java.lang.Thread") != 0) {
                if (j >= depth)
                    return ste.getClassName();
                j++;
            }
        }
        throw new IllegalArgumentException("Invalid depth");
    }

    @SneakyThrows
    public static Class<?> retrieveCallerClass(int depth) {
        return Class.forName(retrieveCallerClassName(depth));
    }

    @Override
    @SneakyThrows
    protected final Object call0(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>[] parameters) {
        String[] callParts = call.split(Pattern.quote("."));

        var src = ReflectUtilImpl.class.getProtectionDomain().getCodeSource();

        if (getCallerClass0(0).getProtectionDomain().getCodeSource() == src) {
            // internal reflective access, permitting
            src = null;
        }

        for (int i = 0; i < callParts.length; i++) {

            // block reflective access to uLib
            if (src == clazz.getProtectionDomain().getCodeSource())
                throw new SecurityException();

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

    @Override
    protected String getCallerClassName0(int depth) {
        return retrieveCallerClassName(depth);
    }

    @Override
    protected Class<?> getCallerClass0(int depth) {
        return retrieveCallerClass(depth);
    }
}
