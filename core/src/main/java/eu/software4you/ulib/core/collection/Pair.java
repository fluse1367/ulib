package eu.software4you.ulib.core.collection;

import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An object able to hold 2 values.
 *
 * @param <T> the type of the first value
 * @param <U> the type of the second value
 */
@SuppressWarnings({"unchecked", "ConstantConditions"})
public sealed class Pair<T, U> extends FixedList<Object> permits Triple, Param {

    /**
     * Creates an immutable pair with initial values.
     *
     * @param t the first value
     * @param u the second value
     */
    public Pair(@Nullable T t, @Nullable U u) {
        this(t, u, false);
    }

    /**
     * Creates a pair with initial values.
     *
     * @param t       the first value
     * @param u       the second value
     * @param mutable if the object should be mutable
     */
    public Pair(@Nullable T t, @Nullable U u, boolean mutable) {
        this(mutable, new Object[]{t, u});
    }

    /**
     * Creates a mutable pair with no initial values.
     */
    public Pair() {
        this(2);
    }

    protected Pair(boolean mutable, @NotNull Object[] elements) {
        super(mutable, elements);
    }

    protected Pair(int capacity) {
        super(capacity);
    }

    /**
     * @return the first element
     */
    @Nullable
    public T getFirst() {
        return (T) get(0);
    }

    /**
     * Sets the first element.
     *
     * @param t the element to set
     * @return the previously assigned element
     * @throws UnsupportedOperationException if this pair is immutable
     * @throws IllegalArgumentException      if this pair does not allow empty element and the element is {@code null}
     */
    @Nullable
    public T setFirst(@Nullable T t) {
        return (T) set(0, t);
    }

    /**
     * @return the second element
     */
    @Nullable
    public U getSecond() {
        return (U) get(1);
    }

    /**
     * Sets the second element.
     *
     * @param u the element to set
     * @return the previously assigned element
     * @throws UnsupportedOperationException if this pair is immutable
     * @throws IllegalArgumentException      if this pair does not allow empty element and the element is {@code null}
     */
    @Nullable
    public U setSecond(@Nullable U u) {
        return (U) set(1, u);
    }

    @Override
    @Nullable
    public Object set(int index, @Nullable Object element) {
        if (!Pair.class.isAssignableFrom(ReflectUtil.getCallerClass()))
            throw new UnsupportedOperationException("Direct write-access is not supported");
        return super.set(index, element);
    }
}
