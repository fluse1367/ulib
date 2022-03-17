package eu.software4you.ulib.core.function;

import lombok.SneakyThrows;

/**
 * A task that takes three parameters, does not return a result and may throw a throwable object.
 *
 * @apiNote only pass this task object as consumer if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface TriParamTask<T, U, V, X extends Throwable> extends TriConsumer<T, U, V> {
    void execute(T t, U u, V v) throws X;

    @SneakyThrows
    @Override
    default void apply(T t, U u, V v) {
        execute(t, u, v);
    }
}
