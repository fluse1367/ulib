package eu.software4you.spigot.mappings;

import eu.software4you.reflect.Parameter;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Represents a name-obfuscated method.
 */
public interface ObfMethod extends Obf<Method> {
    /**
     * Returns the method's return type.
     *
     * @return the method's return type.
     */
    @NotNull
    ObfClass getReturnType();

    /**
     * Returns the method's parameters types.
     *
     * @return the method's parameters types
     */
    @NotNull
    ObfClass[] getParameterTypes();

    /**
     * Generates a parameters list for this method, ready for use in {@link eu.software4you.reflect.ReflectUtil}.
     *
     * @param params the parameters
     * @return the parameter list
     * @throws IllegalArgumentException  if a parameters is of wrong type
     * @throws IndexOutOfBoundsException if the method has more params than provided
     * @see eu.software4you.reflect.ReflectUtil#call(String, Object, String, List[])
     */
    default List<Parameter<?>> asParams(Object... params) {
        AtomicInteger i = new AtomicInteger(0);
        return Arrays.stream(getParameterTypes())
                .map(ObfClass::find)
                .map(cl -> {
                    Object param = params[i.getAndIncrement()];
                    Validate.isAssignableFrom(cl, param.getClass(),
                            "%s cannot be casted to %s", param, cl.getName());
                    return (Parameter<?>) new Parameter(cl, param);
                }).collect(Collectors.toList());
    }
}
