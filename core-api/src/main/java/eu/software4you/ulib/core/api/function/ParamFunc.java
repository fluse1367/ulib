package eu.software4you.ulib.core.api.function;

import lombok.SneakyThrows;

import java.util.function.Function;

/**
 * A task that takes one parameter, returns a value and may throw a throwable object.
 *
 * @apiNote only pass this task object as function if you know for certain it won't throw an exception
 */
@FunctionalInterface
public interface ParamFunc<T, R> extends Function<T, R> {
    R execute(T t) throws Throwable;

    @SneakyThrows
    @Override
    default R apply(T t) {
        return execute(t);
    }
}
