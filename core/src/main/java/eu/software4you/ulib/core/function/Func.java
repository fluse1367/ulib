package eu.software4you.ulib.core.function;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;
import java.util.function.Supplier;

/**
 * A task that returns a value and may throw a throwable object.
 *
 * @apiNote only pass this task object as supplier if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface Func<T, X extends Exception> extends Callable<T>, Supplier<T> {
    T execute() throws X;

    @Override
    default T call() throws Exception {
        return execute();
    }

    @SneakyThrows
    @Override
    default T get() {
        return execute();
    }
}
