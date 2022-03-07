package eu.software4you.ulib.core.api.function;

import lombok.SneakyThrows;

import java.util.function.BiConsumer;

/**
 * A task that takes two parameters, does not return a result and may throw a throwable object.
 *
 * @apiNote only pass this task object as consumer if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface BiParamTask<T, U, X extends Throwable> extends BiConsumer<T, U> {
    void execute(T t, U u) throws X;

    @SneakyThrows
    @Override
    default void accept(T t, U u) {
        execute(t, u);
    }
}
