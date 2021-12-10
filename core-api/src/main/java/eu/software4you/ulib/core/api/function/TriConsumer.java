package eu.software4you.ulib.core.api.function;

/**
 * Just like a {@link java.util.function.BiConsumer} but with 1 additional argument.
 *
 * @param <A> first argument type
 * @param <B> second argument type
 * @param <C> third argument type
 * @see java.util.function.BiConsumer
 * @see java.util.function.Consumer
 */
@FunctionalInterface
public interface TriConsumer<A, B, C> {
    /**
     * Applies the operation to the given arguments.
     *
     * @param a the first argument
     * @param b the second argument
     * @param c the third argument
     */
    void apply(A a, B b, C c);
}
