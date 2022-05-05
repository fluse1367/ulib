package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.function.Func;
import eu.software4you.ulib.core.function.ParamTask;
import eu.software4you.ulib.core.impl.value.LazyValueImpl;
import eu.software4you.ulib.core.impl.value.NoSetLazyValue;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Essentially a lazy value is just a cached value. A regular lazy value has the ability to autonomously
 * obtain/cache ("fetch") the value and to inform a third party ("push") about a value update.
 *
 * <p>
 * A <b>push-only</b> lazy value, is a lazy value that is not able to autonomously obtain a value (see {@link #isAvailable()}).
 * <p>
 * A <b>fetch-only</b> lazy value, is a lazy value that will not perform any pushing action when an {@link #set(Object) external value update} is received.
 *
 * @param <T> the value type
 */
public interface LazyValue<T> {

    /**
     * Constructs a lazy value with no initial value.
     *
     * @param fetch the fetch function
     * @param push  the push function
     * @param <T>   the value type
     * @return the newly constructed object
     */
    @NotNull
    static <T> LazyValue<T> of(@NotNull Func<T, ?> fetch, @NotNull ParamTask<T, ?> push) {
        return of(null, fetch, push);
    }

    /**
     * Constructs a lazy value.
     *
     * @param val   the initial value
     * @param fetch the fetch function
     * @param push  the push function
     * @param <T>   the value type
     * @return the newly constructed object
     */
    @NotNull
    static <T> LazyValue<T> of(@Nullable T val, @NotNull Func<T, ?> fetch, @NotNull ParamTask<T, ?> push) {
        return new LazyValueImpl<>(val, Objects.requireNonNull(fetch), Objects.requireNonNull(push));
    }

    /**
     * Constructs a <b>push-only</b> lazy value.
     *
     * @param value        the initial value
     * @param pushFunction the push function
     * @param <T>          the value type
     * @return the newly constructed object
     */
    @NotNull
    static <T> LazyValue<T> of(@Nullable T value, @NotNull ParamTask<T, ?> pushFunction) {
        return new LazyValueImpl<>(value, null, Objects.requireNonNull(pushFunction));
    }

    /**
     * Constructs a <b>fetch-only</b> lazy value with no initial value.
     *
     * @param fetchFunction the fetch function
     * @param <T>           the value type
     * @return the newly constructed object
     */
    @NotNull
    static <T> LazyValue<T> of(@NotNull Func<T, ?> fetchFunction) {
        return of(null, fetchFunction);
    }

    /**
     * Constructs a <b>fetch-only</b> lazy value.
     *
     * @param value         the initial value
     * @param fetchFunction the fetch function
     * @param <T>           the value type
     * @return the newly constructed object
     */
    @NotNull
    static <T> LazyValue<T> of(@Nullable T value, @NotNull Func<T, ?> fetchFunction) {
        return new LazyValueImpl<>(value, Objects.requireNonNull(fetchFunction), null);
    }

    /**
     * Constructs a <b>fetch-only</b> lazy value that cannot receive external value updates,
     * however it can still be {@link #clear() cleared}.
     *
     * @param fetchFunction the fetch function
     * @param <T>           the value type
     * @return the newly constructed object
     */
    @NotNull
    static <T> LazyValue<T> immutable(@NotNull Func<T, ?> fetchFunction) {
        return new NoSetLazyValue<>(null, Objects.requireNonNull(fetchFunction), null);
    }


    /**
     * Obtains the value.
     *
     * @return the value
     * @throws NoSuchElementException if no value is available as specified by {@link #isAvailable()}
     * @apiNote {@code null} is also a valid value
     */
    @Nullable
    T get() throws NoSuchElementException;

    /**
     * Returns the value if it is available as specified by {@link #isAvailable()}.
     *
     * @return an optional wrapping the value
     */
    @NotNull
    default Optional<T> getIfAvailable() {
        return isAvailable() ? Optional.of(Objects.requireNonNull(get())) : Optional.empty();
    }

    /**
     * Returns the value if it is present as specified by {@link #isPresent()}.
     *
     * @return an optional wrapping the value
     */
    @NotNull
    default Optional<T> getIfPresent() {
        return isPresent() ? Optional.of(Objects.requireNonNull(get())) : Optional.empty();
    }


    /**
     * Deletes the underlying value.
     * <p>
     * Directly executing {@link #isPresent()} after this method will result in {@code false}.
     */
    void clear();

    /**
     * Determines if an underlying value is either present at the time of calling
     * or can be autonomously obtained using an underlying fetching function.
     *
     * @return {@code true} if a value is available, {@code false} otherwise
     */
    boolean isAvailable();

    /**
     * @return {@code true} if a value is currently present, {@code false} otherwise
     */
    boolean isPresent();

    /**
     * @return {@code true} if the underlying fetching function is currently running, {@code false} otherwise
     */
    boolean isRunning();

    /**
     * Sets the underlying value.
     * <p>
     * If a pushing function is present the value will be respectively pushed.
     *
     * @param t the value to set
     * @return the given value
     */
    @Nullable
    @Contract("!null -> !null")
    T set(@Nullable T t);
}
