package eu.software4you.ulib.core.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object able to hold 3 values.
 *
 * @param <T> the type of the first value
 * @param <U> the type of the second value
 * @param <V> the type of the third value
 */
@SuppressWarnings({"ConstantConditions", "unchecked"})
public sealed class Triple<T, U, V> extends Pair<T, U> permits Quadruple {
    /**
     * Creates an immutable triple with initial values.
     *
     * @param t the first value
     * @param u the second value
     * @param v the third value
     */
    public Triple(@Nullable T t, @Nullable U u, @Nullable V v) {
        this(t, u, v, false);
    }

    /**
     * Creates a triple with initial values.
     *
     * @param t       the first value
     * @param u       the second value
     * @param v       the third value
     * @param mutable if the object should be mutable
     */
    public Triple(@Nullable T t, @Nullable U u, @Nullable V v, boolean mutable) {
        this(mutable, new Object[]{t, u, v});
    }

    /**
     * Creates a mutable triple with no initial values.
     */
    public Triple() {
        this(3);
    }

    protected Triple(boolean mutable, @NotNull Object[] elements) {
        super(mutable, elements);
    }

    protected Triple(int capacity) {
        super(capacity);
    }

    /**
     * @return the third element
     */
    public V getThird() {
        return (V) get(2);
    }

    /**
     * Sets the third element.
     *
     * @param v the element to set
     * @return the previously assigned element
     * @throws UnsupportedOperationException if this pair is immutable
     * @throws IllegalArgumentException      if this pair does not allow empty element and the element is {@code null}
     */
    public V setThird(V v) {
        return (V) set(2, v);
    }
}
