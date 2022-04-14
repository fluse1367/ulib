package eu.software4you.ulib.core.function;

import lombok.SneakyThrows;

/**
 * A task that takes one parameter, returns a value and may throw a throwable object.
 *
 * @apiNote only pass this task object as function if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface TriParamFunc<T, U, V, R, X extends Exception> extends TriFunction<T, U, V, R> {
    R execute(T t, U u, V v) throws X;

    @SneakyThrows
    @Override
    default R apply(T t, U u, V v) {
        return execute(t, u, v);
    }
}
