package eu.software4you.ulib.core.collection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object able to hold 4 values.
 *
 * @param <T> the type of the first value
 * @param <U> the type of the second value
 * @param <V> the type of the third value
 * @param <W> the type of the fourth value
 */
@SuppressWarnings({"ConstantConditions", "unchecked"})
public final class Quadruple<T, U, V, W> extends Triple<T, U, V> {
    /**
     * Creates an immutable quadruple with initial values.
     *
     * @param t the first value
     * @param u the second value
     * @param v the third value
     * @param w the fourth value
     */
    public Quadruple(@Nullable T t, @Nullable U u, @Nullable V v, @Nullable W w) {
        this(t, u, v, w, false);
    }

    /**
     * Creates a quadruple with initial values.
     *
     * @param t       the first value
     * @param u       the second value
     * @param v       the third value
     * @param w       the fourth value
     * @param mutable if the object should be mutable
     */
    public Quadruple(@Nullable T t, @Nullable U u, @Nullable V v, @Nullable W w, boolean mutable) {
        this(mutable, new Object[]{t, u, v, w});
    }

    /**
     * Creates a mutable quadruple with no initial values.
     */
    public Quadruple() {
        this(4);
    }

    protected Quadruple(boolean mutable, @NotNull Object[] elements) {
        super(mutable, elements);
    }

    protected Quadruple(int capacity) {
        super(capacity);
    }

    /**
     * @return the fourth element
     */
    @Nullable
    public W getFourth() {
        return (W) get(3);
    }

    /**
     * Sets the fourth element.
     *
     * @param w the element to set
     * @return the previously assigned element
     * @throws UnsupportedOperationException if this pair is immutable
     * @throws IllegalArgumentException      if this pair does not allow empty element and the element is {@code null}
     */
    @Nullable
    public W setFourth(@Nullable W w) {
        return (W) set(3, w);
    }
}
