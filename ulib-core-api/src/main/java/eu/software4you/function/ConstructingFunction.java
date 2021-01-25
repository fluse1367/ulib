package eu.software4you.function;

import java.util.function.Function;

@FunctionalInterface
public interface ConstructingFunction<R> extends Function<Object[], R> {
}
