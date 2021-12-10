package eu.software4you.ulib.core.api.function;

/**
 * Just like a {@link java.util.function.BiFunction} but with 1 additional argument.
 *
 * @param <A> first argument type
 * @param <B> second argument type
 * @param <C> third argument type
 * @param <R> return type
 * @see java.util.function.BiFunction
 * @see java.util.function.Function
 */
@FunctionalInterface
public interface TriFunction<A, B, C, R> {
    /**
     * Applies the function to the given arguments.
     *
     * @param a the first argument
     * @param b the second argument
     * @param c the third argument
     * @return the result
     */
    R apply(A a, B b, C c);
}
