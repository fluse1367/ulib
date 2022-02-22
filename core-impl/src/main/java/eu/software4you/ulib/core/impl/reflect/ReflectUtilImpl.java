package eu.software4you.ulib.core.impl.reflect;

import eu.software4you.ulib.core.api.reflect.Parameter;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public final class ReflectUtilImpl extends ReflectUtil {

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

                var opMethod = findUnderlyingMethod(clazz, callParts[i], forced, argumentTypes);
                if (opMethod.isEmpty())
                    throw new NoSuchMethodException(String.format("%s(%s) in %s (%sforced)", callParts[i], ArrayUtils.toString(argumentTypes), clazz, forced ? "" : "not "));
                var method = opMethod.get();
                if (forced)
                    method.setAccessible(true);
                returned = method.invoke(invoker, arguments);
                returnType = method.getReturnType();
            } else {
                // the call is a field
                var opField = findUnderlyingField(clazz, callParts[i], forced);
                if (opField.isEmpty())
                    throw new NoSuchFieldException(String.format("%s in %s (%sforced)", callParts[i], clazz, forced ? "" : "not "));
                var field = opField.get();
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


        Class<?>[] stack = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(stream -> stream
                        .map(StackWalker.StackFrame::getDeclaringClass)
                        .toArray(Class[]::new)
                );

        /*
        -1: [0] this
         0: [1] caller
        ...
         */

        int offset = 1;
        int i = depth + offset;
        if (i >= stack.length)
            throw new IllegalArgumentException("Depth (" + depth + ") > Stack (" + (stack.length - (offset + 1)) + ")");

        return stack[i];
    }

    @Override
    protected Class<?> getCallerClass0(int depth) {
        return retrieveCallerClass(++depth);
    }
}
