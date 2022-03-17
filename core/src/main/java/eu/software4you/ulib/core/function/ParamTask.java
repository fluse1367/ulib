package eu.software4you.ulib.core.function;

import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * A task that takes one parameter, does not return a result and may throw a throwable object.
 *
 * @apiNote only pass this task object as consumer if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface ParamTask<T, X extends Throwable> extends Consumer<T> {
    void execute(T t) throws X;

    @SneakyThrows
    @Override
    default void accept(T t) {
        execute(t);
    }
}
