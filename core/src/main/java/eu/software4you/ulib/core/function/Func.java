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
public interface Func<T, X extends Throwable> extends Callable<T>, Supplier<T> {
    T execute() throws X;

    @Override
    default T call() throws Exception {
        try {
            return execute();
        } catch (Throwable t) {
            throw new Exception(t);
        }
    }

    @SneakyThrows
    @Override
    default T get() {
        return execute();
    }
}
