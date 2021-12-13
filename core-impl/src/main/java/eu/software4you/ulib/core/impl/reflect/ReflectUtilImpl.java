package eu.software4you.ulib.core.impl.reflect;

import eu.software4you.ulib.core.api.reflect.Parameter;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.utils.ClassUtils;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class ReflectUtilImpl extends ReflectUtil {

    private static final SecurityManager sec = new SecurityManager();

    @Override
    @SneakyThrows
    protected final Object call0(Class<?> clazz, Object invoker, String call, boolean forced, List<Parameter<?>>[] parameters) {
        String[] callParts = call.split(Pattern.quote("."));

        for (int i = 0; i < callParts.length; i++) {

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
                    throw new NoSuchMethodException(String.format("%s(%s) in %s (%sforced)", callParts[i], ArrayUtils.toString(argumentTypes), clazz, forced ? "" : "not "));
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
                    throw new NoSuchFieldException(String.format("%s in %s (%sforced)", callParts[i], clazz, forced ? "" : "not "));
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

    public static Class<?> retrieveCallerClass(int depth) {
        if (depth < 0)
            throw new IllegalArgumentException("Depth < 0 (" + depth + ")");

        Class<?>[] stack = sec.getClassContext();

        /*
        -2: [0] SecurityManager#getClassContext
        -1: [1] #printStackContext
         0: [2] caller
        ...
         */

        int i = depth + 2;
        if (i >= stack.length)
            throw new IllegalArgumentException("Depth ( " + depth + ") > Stack (" + (stack.length - 3) + ")");

        return stack[i];
    }

    @Override
    protected Class<?> getCallerClass0(int depth) {
        return retrieveCallerClass(++depth);
    }

    private static class SecurityManager extends java.lang.SecurityManager {
        @SuppressWarnings("rawtypes")
        @Override
        public Class[] getClassContext() {
            return super.getClassContext();
        }
    }
}
