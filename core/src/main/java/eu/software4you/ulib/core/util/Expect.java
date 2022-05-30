package eu.software4you.ulib.core.util;

import eu.software4you.ulib.core.function.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


/**
 * Expect the unexpected.
 * <p>
 * Wraps an optional value or exception. Intended for use as result of an execution.
 * An Expect object may never have a value and a exception contained at the same time.
 * <p>
 * This class can be seen as {@link Optional} with additional exception wrapping.
 *
 * @param <T> the result type
 * @param <X> the type of exception
 * @see Optional
 * @see CompletableFuture
 */
@SuppressWarnings("ClassCanBeRecord")
public final class Expect<T, X extends Exception> {
    private static final Expect<?, ?> EMPTY = new Expect<>(null, null);

    /**
     * Returns an empty Expect instance.
     *
     * @param <T> the expected result type
     * @return an empty expect instance
     */
    @NotNull
    @Contract(pure = true)
    public static <T, X extends Exception> Expect<T, X> empty() {
        //noinspection unchecked
        return (Expect<T, X>) EMPTY;
    }

    /**
     * Constructs an Expect object wrapping a non-{@code null} value.
     *
     * @param value the value to wrap
     * @param <T>   the value type
     * @return the newly constructed Expect object
     * @throws NullPointerException if the supplied value is {@code null}.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T, X extends Exception> Expect<T, X> of(@NotNull T value) {
        return new Expect<>(Objects.requireNonNull(value), null);
    }

    /**
     * Constructs an Expect object wrapping the supplied value if it is not {@code null}, otherwise returns an empty optional.
     *
     * @param value the value to wrap
     * @param <T>   the type of value
     * @return the newly constructed Expect object or empty Expect object
     */
    @NotNull
    @Contract(value = "!null -> new", pure = true)
    public static <T, X extends Exception> Expect<T, X> ofNullable(@Nullable T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Constructs an Expect object wrapping a non-{@code null} exception.
     *
     * @param exception the exception to wrap
     * @param <T>       the expected return type
     * @return the newly constructed Expect object
     * @throws NullPointerException if the supplied exception is {@code null}.
     */
    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static <T, X extends Exception> Expect<T, X> failed(@NotNull X exception) {
        return new Expect<>(null, Objects.requireNonNull(exception));
    }

    /**
     * Executes the supplied non-{@code null} task, catching any exception.
     *
     * @param task the task to execute
     * @param <T>  the result type
     * @return an Expect object wrapping the task's result
     * @throws NullPointerException if the supplied task object is {@code null}
     */
    @NotNull
    public static <T, X extends Exception> Expect<T, X> compute(@NotNull Func<T, X> task) {
        Objects.requireNonNull(task);

        try {
            return ofNullable(task.execute());
        } catch (Exception e) {
            return new Expect<>(null, e);
        }
    }

    @NotNull
    public static <T, R, X extends Exception> Expect<R, X> compute(@NotNull ParamFunc<T, R, X> func, @Nullable T t) {
        return compute(() -> func.execute(t));
    }

    @NotNull
    public static <T, U, R, X extends Exception> Expect<R, X> compute(@NotNull BiParamFunc<T, U, R, X> func, @Nullable T t, @Nullable U u) {
        return compute(() -> func.execute(t, u));
    }

    @NotNull
    public static <T, U, V, R, X extends Exception> Expect<R, X> compute(@NotNull TriParamFunc<T, U, V, R, X> func, @Nullable T t, @Nullable U u, @Nullable V v) {
        return compute(() -> func.execute(t, u, v));
    }


    /**
     * Executes the supplied non-{@code null} task, catching any thrown exception.
     *
     * @param task the task to execute
     * @return an empty Expect object on success, or an Expect object wrapping the thrown object on failure
     * @throws NullPointerException if the supplied task object is {@code null}
     */
    @NotNull
    public static <X extends Exception> Expect<Void, X> compute(@NotNull Task<X> task) {
        Objects.requireNonNull(task);

        try {
            task.execute();
        } catch (Exception e) {
            return new Expect<>(null, e);
        }
        return empty();
    }

    private final T value;
    private final Exception caught;

    private Expect(T value, Exception caught) {
        this.value = value;
        this.caught = caught;
    }

    /**
     * Returns an optional wrapping the contained value if no exception is present.
     *
     * @return an optional wrapping the contained value
     */
    @NotNull
    @Contract(pure = true)
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    /**
     * Checks if a value is present.
     *
     * @return {@code true} if a value is present, {@code false} otherwise
     */
    @Contract(pure = true)
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Checks if no value is present.
     *
     * @return {@code true} if no value is present, {@code false} otherwise
     */
    @Contract(pure = true)
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Checks if a exception is present.
     *
     * @return {@code true} if a exception object is present, {@code false} otherwise
     */
    @Contract(pure = true)
    public boolean hasCaught() {
        return caught != null;
    }

    /**
     * Returns an optional wrapping the caught object.
     *
     * @return an optional wrapping the caught object
     */
    @NotNull
    public Optional<Exception> getCaught() {
        return Optional.ofNullable(caught);
    }

    /**
     * Returns an optional wrapping the caught object.
     * <p>
     * If the caught object is not from a specific type, an empty optional will be returned.
     *
     * @param type the requested type
     * @return an optional wrapping the caught object
     */
    @NotNull
    public <XX extends Exception> Optional<XX> getCaught(@NotNull Class<XX> type) {
        return hasCaught() && type.isInstance(caught) ? Optional.of(type.cast(caught)) : Optional.empty();
    }

    /**
     * Returns the contained value.
     *
     * @return the contained value
     */
    @Nullable
    @Contract(pure = true)
    public T getValue() {
        return value;
    }

    /**
     * Throws the caught object if present.
     */
    @Contract(pure = true)
    public void rethrow() throws Exception {
        if (!hasCaught())
            return;
        throw caught;
    }

    /**
     * Throws the caught object if present.
     * <p>
     * If the caught object is not from a specific type, it will be thrown wrapped in a runtime exception.
     *
     * @param type the type to throw
     */
    @Contract(pure = true)
    public <XX extends Exception> void rethrow(@NotNull Class<XX> type) throws XX, RuntimeException {
        if (!hasCaught())
            return;

        if (type.isInstance(caught))
            throw type.cast(caught);

        // exception is not of expected type, box in RuntimeException
        throw caught instanceof RuntimeException re ?
                re // attempt direct throw*
                : new RuntimeException(caught); // throw boxed
    }

    /**
     * Throws the caught object wrapped in a runtime exception if present.
     */
    @Contract(pure = true)
    public void rethrowRE() throws RuntimeException {
        rethrow(RuntimeException.class);
    }

    /**
     * Returns the contained value if it is present and throws an exception if no value is present.
     *
     * @return the contained value
     * @throws NoSuchElementException if no exception and no value is present
     * @throws IllegalStateException  if a exception and no value is present
     */
    @NotNull
    @Contract(pure = true)
    public T orElseThrow() throws NoSuchElementException, IllegalStateException {
        if (isEmpty())
            throw hasCaught() ? new IllegalStateException("Execution failed", caught) : new NoSuchElementException("No value present");

        return value;
    }

    /**
     * Returns the contained value if it is present and throws an exception if no value is present.
     *
     * @param <XX>              the type of exception the supplier is supplying
     * @param exceptionSupplier a supplier that supplies a exception
     * @return the contained value
     * @throws XX if no value is present
     */
    @NotNull
    public <XX extends Exception> T orElseThrow(@NotNull Supplier<XX> exceptionSupplier) throws XX {
        if (isEmpty())
            throw exceptionSupplier.get();

        return value;
    }

    /**
     * Returns the contained value if it is present and attempts to rethrow the caught exception.
     * If this is an empty Expect object a {@link NoSuchElementException} is thrown instead.
     *
     * @return the contained value
     * @throws X if no value is present
     */
    @NotNull
    @Contract(pure = true)
    public T orElseRethrow() throws Exception {
        return orElseRethrow(Exception.class);
    }

    /**
     * Returns the contained value if it is present and attempts to rethrow the caught exception if it is from a specific type.
     * If this is an empty Expect object a {@link NoSuchElementException} is thrown instead.
     *
     * @param type the throw type
     * @return the contained value
     */
    @NotNull
    @Contract(pure = true)
    public <XX extends Exception> T orElseRethrow(@NotNull Class<XX> type) throws XX, NoSuchElementException {
        if (isPresent())
            return value;

        rethrow(type);
        throw new NoSuchElementException("Empty Expect object");
    }

    /**
     * Executes the supplied task if a value is present and catches any exception.
     *
     * @param task the task to execute
     * @return an Expect object wrapping a potential caught exception
     */
    @NotNull
    public <XX extends Exception> Expect<Void, XX> ifPresent(@NotNull ParamTask<? super T, XX> task) {
        Objects.requireNonNull(task);

        return map(val -> {
            task.execute(val);
            return null;
        });
    }

    /**
     * Executes the supplied task if a value is present, or executes the alternative task if no value is present and catches and exception.
     *
     * @param task  the task to execute
     * @param other the alternative task to execute
     * @return an Expect object wrapping a potential caught exception
     */
    @NotNull
    public <XX extends Exception> Expect<Void, XX> ifPresentOrElse(@NotNull ParamTask<? super T, XX> task,
                                                                   @NotNull ParamTask<? super Optional<? extends Exception>, XX> other) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(other);

        return isPresent() ? compute(() -> task.execute(value)) : compute(() -> other.execute(getCaught()));
    }

    /**
     * Returns the wrapped value if it is present, the supplied value otherwise.
     *
     * @param other the other value to return if there is no wrapped value present
     * @return the underlying value (if present), the supplied value otherwise
     */
    @Nullable
    @Contract(value = "!null -> !null", pure = true)
    public T orElse(@Nullable T other) {
        return isPresent() ? value : other;
    }

    /**
     * Executes the supplied task if no value is present and catches any exception.
     *
     * @param task the task to execute if no value is present
     * @return this Except object if a value is present, otherwise another Expect object wrapping a potential caught exception or value from the task's execution
     */
    @NotNull
    public <XX extends Exception> Expect<T, ? extends Exception> orElseGet(@NotNull Func<T, XX> task) {
        Objects.requireNonNull(task);

        return isPresent() ? this : compute(task);
    }

    /**
     * Executes the supplied task if no value is present and catches any exception.
     *
     * @param task the task to execute if no value is present
     * @return this Except object if a value is present, otherwise another Expect object wrapping a potential caught exception or value from the task's execution
     */
    @NotNull
    public <XX extends Exception> Expect<T, ? extends Exception> orElseGet(@NotNull ParamFunc<? super Optional<? extends Exception>, T, XX> task) {
        Objects.requireNonNull(task);

        return isPresent() ? this : compute(() -> task.execute(getCaught()));
    }

    /**
     * Executes the supplied task if a value is present and catches and exception.
     *
     * @param mapper the task to execute if a value is present
     * @param <U>    the return type
     * @return an empty Expect object if no value is present, otherwise another Expect object wrapping a potential caught exception or value from the task's execution
     */
    @NotNull
    public <U, XX extends Exception> Expect<U, XX> map(@NotNull ParamFunc<? super T, U, XX> mapper) {
        Objects.requireNonNull(mapper);

        return isPresent() ? compute(() -> mapper.execute(value))
                : hasCaught() ? new Expect<>(null, new IllegalStateException("Previous execution failed", caught))
                : empty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, caught);
    }
}
