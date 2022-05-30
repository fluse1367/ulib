package eu.software4you.ulib.core.collection;

import eu.software4you.ulib.core.util.Conditions;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * A list with a fixed amount of elements.
 * <p>
 * A fixed list does not support adding elements at arbitrary positions.
 */
public class FixedList<E> extends AbstractList<E> {

    @Getter
    private final int capacity;
    /**
     * Indicates if this list allows empty ({@code null}) elements.
     */
    @Getter
    @Accessors(fluent = true)
    private final boolean hasEmptyElements;
    /**
     * Indicates if this list allows modification.
     */
    @Getter
    private final boolean mutable;
    private final Object[] elements;
    @Getter
    @Accessors(fluent = true)
    private int size;

    /**
     * Creates a list that allows modification and empty elements.
     *
     * @param capacity the capacity of the list
     */
    public FixedList(int capacity) {
        if (capacity < 0)
            throw new IllegalArgumentException("Capacity may not be lower than 0 (%d)".formatted(capacity));

        this.hasEmptyElements = true;
        this.mutable = true;

        this.capacity = capacity;
        this.elements = new Object[capacity];
        this.size = 0;
    }

    /**
     * Creates a list with an initial element array.
     *
     * @param mutable  if the list should allow modification
     * @param elements the initial element array
     */
    public FixedList(boolean mutable, @NotNull E[] elements) {
        Objects.requireNonNull(elements);

        this.elements = elements.clone();
        this.size = this.capacity = elements.length;
        this.mutable = mutable;
        this.hasEmptyElements = !Conditions.nNil((Object[]) elements);
    }

    @Override
    @Nullable
    public E get(int index) {
        Objects.checkIndex(index, size);

        @SuppressWarnings("unchecked")
        E e = (E) elements[index];
        if (!hasEmptyElements && Conditions.nil(e))
            throw new IllegalStateException("Element %d is empty".formatted(index));

        return e;
    }

    @Override
    @Nullable
    public E set(int index, @Nullable E element) {
        ensureMutability(index, element);

        @SuppressWarnings("unchecked")
        E e = (E) elements[index];
        elements[index] = element;

        // grow size if a new element was added
        if (Conditions.nil(e))
            size++;

        return e;
    }

    @Override
    public boolean add(@Nullable E e) {
        set(size, e);
        return true;
    }

    @Override
    @Contract("_, _ -> fail")
    public boolean addAll(int index, @Nullable Collection<? extends E> c) {
        throw new UnsupportedOperationException(); // no adding at arbitrary indices
    }

    @Override
    @Nullable
    public E remove(int index) {
        var e = set(index, null);
        size--;
        return e;
    }

    private void ensureMutability() {
        if (!mutable)
            throw new UnsupportedOperationException();
    }

    private void ensureMutability(int i, E e) {
        ensureMutability();

        Objects.checkIndex(i, capacity);

        // ensure inserted element is not empty
        if (!hasEmptyElements && Conditions.nil(e))
            throw new IllegalArgumentException("Element is empty");
    }
}
