package eu.software4you.ulib.core.api.function;

import lombok.SneakyThrows;

/**
 * A task that takes one parameter, returns a value and may throw a throwable object.
 *
 * @apiNote only pass this task object as function if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface TriParamFunc<T, U, V, R> extends TriFunction<T, U, V, R> {
    R execute(T t, U u, V v) throws Throwable;

    @SneakyThrows
    @Override
    default R apply(T t, U u, V v) {
        return execute(t, u, v);
    }
}
