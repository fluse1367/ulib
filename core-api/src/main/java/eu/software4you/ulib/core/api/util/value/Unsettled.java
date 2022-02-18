package eu.software4you.ulib.core.api.util.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A result of an execution.
 *
 * @param <T> the type of resulting value
 */
public class Unsettled<T> {

    private static final Unsettled<?> NONE = new Unsettled<>(null, null);

    /**
     * Constructs a new unsettled object with a successful result.
     *
     * @param value the value to hold, may be null
     * @param <T>   the type of value
     * @return the newly constructed object, or the static empty unsettled object if {@code value} is null
     */
    public static <T> Unsettled<T> of(@Nullable T value) {
        //noinspection unchecked
        return value == null ? (Unsettled<T>) NONE : new Unsettled<>(value, null);
    }

    /**
     * Constructs a new unsettled object with a not successful result.
     *
     * @param thr the exception that was thrown during execution
     * @param <T> the type of the expected result
     * @return the newly constructed object
     */
    public static <T> Unsettled<T> thrown(@NotNull Throwable thr) {
        return new Unsettled<>(null, Objects.requireNonNull(thr));
    }

    /**
     * Executes a function. Any object thrown will be caught.
     *
     * @param func the function to execute
     * @param <R>  the return type of the function
     * @return an unsettled object containing the result or thrown object
     */
    public static <R> Unsettled<R> execute(Supplier<R> func) {
        try {
            return Unsettled.of(func.get());
        } catch (Throwable t) {
            return Unsettled.thrown(t);
        }
    }

    protected final T value;
    protected final Throwable thrown;

    protected Unsettled(T value, Throwable thrown) {
        this.value = value;
        this.thrown = thrown;
    }

    /**
     * @return {@code true} if the execution was a success and there is no thrown object present, {@code false} otherwise
     */
    public boolean wasSuccess() {
        return thrown == null;
    }

    /**
     * @return {@code true} if the execution was not successful and there is a thrown object present, {@code false} otherwise
     */
    public boolean wasFailure() {
        return thrown != null;
    }

    /**
     * @return an optional wrapping the actual value, may be empty
     */
    public Optional<T> get() {
        return Optional.ofNullable(value);
    }

    /**
     * @return an optional wrapping the thrown object, may be empty
     */
    public Optional<Throwable> getThrown() {
        return Optional.ofNullable(thrown);
    }

}
