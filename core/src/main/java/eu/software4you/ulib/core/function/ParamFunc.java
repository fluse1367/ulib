package eu.software4you.ulib.core.function;

import lombok.SneakyThrows;

import java.util.function.Function;

/**
 * A task that takes one parameter, returns a value and may throw a throwable object.
 *
 * @apiNote only pass this task object as function if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface ParamFunc<T, R, X extends Exception> extends Function<T, R> {
    R execute(T t) throws X;

    @SneakyThrows
    @Override
    default R apply(T t) {
        return execute(t);
    }
}
