package eu.software4you.ulib.core.function;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;

/**
 * A task that does not return a result and may throw a throwable object.
 *
 * @apiNote only pass this task object as runnable if you certainly know it won't throw an exception
 */
@FunctionalInterface
public interface Task<X extends Exception> extends Callable<Void>, Runnable {
    void execute() throws X;

    @Override
    default Void call() throws Exception {
        execute();
        return null;
    }

    @SneakyThrows
    @Override
    default void run() {
        execute();
    }
}
