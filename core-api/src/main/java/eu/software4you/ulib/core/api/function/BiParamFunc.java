package eu.software4you.ulib.core.api.function;

import lombok.SneakyThrows;

import java.util.function.BiFunction;

/**
 * A task that takes one parameter, returns a value and may throw a throwable object.
 *
 * @apiNote only pass this task object as function if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface BiParamFunc<T, U, R> extends BiFunction<T, U, R> {
    R execute(T t, U u) throws Throwable;

    @SneakyThrows
    @Override
    default R apply(T t, U u) {
        return execute(t, u);
    }
}
