package eu.software4you.function;

import java.util.function.Function;

/**
 * A {@link Function} designated to create a new instance of any object. Can be seen as reference to a constructor.
 *
 * @param <R> type of the instance object
 */
@FunctionalInterface
public interface ConstructingFunction<R> extends Function<Object[], R> {
    /**
     * Creates a new instance.
     *
     * @param objects (usually) the constructor arguments
     * @return the new instance
     */
    @Override
    R apply(Object... objects);
}
