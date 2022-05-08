package eu.software4you.ulib.core.inject;

import org.jetbrains.annotations.*;

import java.util.Optional;

/**
 * Callback information for a hook into a method.<br>
 * If the a return value is provided, the method will be cancelled after the took was processed
 * and the provided value will be returned.<br>
 *
 * @param <T> The return type of the method.
 */
public interface Callback<T> {

    /**
     * Returns the object instance of proxied object (if present).
     */
    @NotNull
    Optional<Object> proxyInst();

    /**
     * Returns the object instance of the hooked method, or {@code null} if the hooked method is static.
     */
    @NotNull
    Optional<Object> self();

    /**
     * Returns the calling class of the hooked method.
     *
     * @return the calling class
     */
    @NotNull
    Class<?> callerClass();

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
     * Note: {@code null} is also a valid return value, so setting it does not clear the return value.
     * Use {@link #clearReturnValue()} for this.
     *
     * @param value the value to set
     * @return the value
     * @throws IllegalStateException if return type is void
     */
    @Nullable
    @Contract("!null->!null; null->null")
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

    /**
     * Immediately throws the provided throwable object.<p>
     * The current and all future hook processing is stopped as well as the hooked method.
     *
     * @param t the object to throw
     */
    void throwNow(Throwable t);
}
