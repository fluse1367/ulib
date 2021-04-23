package eu.software4you.litetransform;

import org.jetbrains.annotations.Nullable;

/**
 * Callback information for a hook into a method.<br>
 * If the a return value is provided, the method will be cancelled after the took was processed
 * and the provided value will be returned.<br>
 *
 * @param <T> The return type of the method.
 */
public interface Callback<T> {
    Object self();

    /**
     * Returns if the callback has a return value stored.<br>
     * This can also be the original return value from the actual method.
     */
    boolean hasReturnValue();

    /**
     * Returns the stored return value.<br>
     * Note: {@code null} is also a valid return value.
     *
     * @throws IllegalStateException if no return value is stored.
     */
    @Nullable
    T getReturnValue();


    /**
     * Sets the return value.<br>
     * Note: null is also a valid return value, so setting it does not clear the return value.
     * Use {@link #clearReturnValue()} for this.
     *
     * @param value the value to set
     * @return the value
     * @throws IllegalAccessError if return type is void
     */
    T setReturnValue(@Nullable T value);

    /**
     * Removes the stored return value.
     */
    void clearReturnValue();


    /**
     * Cancels all future injection processing and immediately returns the method after the current injection has been processed.<br>
     * If the method returns a non-void type, the return value must be set before.
     *
     * @throws IllegalStateException if no return value is provided and the method returns a non-void type
     */
    void cancel();
}
