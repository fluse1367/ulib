package eu.software4you.ulib.minecraft.mappings;

import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Represents a name-mapped method.
 */
public interface MappedMethod extends Mapped<Method> {
    /**
     * Returns the method's return type.
     *
     * @return the method's return type.
     */
    @NotNull
    MappedClass returnType();

    /**
     * Returns the method's parameters types.
     *
     * @return the method's parameters types
     */
    @NotNull
    MappedClass[] parameterTypes();

    /**
     * Generates a parameters list for this method, ready for use in {@link ReflectUtil}.
     *
     * @param params the parameters
     * @return the parameter list
     * @throws IllegalArgumentException  if a parameters is of wrong type
     * @throws IndexOutOfBoundsException if the method has more params than provided
     * @see ReflectUtil#call(Class, Object, String, List[])
     */
    @NotNull
    default List<Param<?>> asParams(@NotNull Object... params) {
        AtomicInteger i = new AtomicInteger(0);
        return Arrays.stream(parameterTypes())
                .map(MappedClass::find)
                .map(Expect::orElseThrow)
                .map(cl -> {
                    Object param = params[i.getAndIncrement()];

                    if (!cl.isAssignableFrom(param.getClass())) {
                        throw new IllegalArgumentException("%s cannot be casted to %s".formatted(param, cl.getName()));
                    }
                    return (Param<?>) new Param<>(cl, param);
                }).collect(Collectors.toList());
    }
}
