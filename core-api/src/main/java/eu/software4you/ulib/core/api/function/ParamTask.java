package eu.software4you.ulib.core.api.function;

import lombok.SneakyThrows;

import java.util.function.Consumer;

/**
 * A task that takes one parameter, does not return a result and may throw a throwable object.
 *
 * @apiNote only pass this task object as consumer if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface ParamTask<T> extends Consumer<T> {
    void execute(T t) throws Throwable;

    @SneakyThrows
    @Override
    default void accept(T t) {
        execute(t);
    }
}
