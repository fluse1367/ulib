package eu.software4you.ulib.core.api.util.value;

import eu.software4you.ulib.core.api.function.Func;
import eu.software4you.ulib.core.api.function.ParamFunc;
import eu.software4you.ulib.core.api.function.ParamTask;
import eu.software4you.ulib.core.api.function.Task;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


/**
 * Expect the unexpected.
 * <p>
 * Wraps an optional value or throwable object. Intended for use as result of an execution.
 * An Expect object may never have a value and a throwable object contained at the same time.
 * <p>
 * This class can be seen as {@link Optional} with additional exception wrapping.
 *
 * @param <T> the result type
 * @param <X> the type of throwable object
 * @see Optional
 * @see CompletableFuture
 */
public final class Expect<T, X extends Throwable> {
    private static final Expect<?, ?> EMPTY = new Expect<>(null, null);

    /**
     * Returns an empty Expect instance.
     *
     * @param <T> the expected result type
     * @return an empty expect instance
     */
    @NonNull
    @Contract(pure = true)
    public static <T, X extends Throwable> Expect<T, X> empty() {
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
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static <T, X extends Throwable> Expect<T, X> of(@NonNull T value) {
        return new Expect<>(Objects.requireNonNull(value), null);
    }

    /**
     * Constructs an Expect object wrapping the supplied value if it is not {@code null}, otherwise returns an empty optional.
     *
     * @param value the value to wrap
     * @param <T>   the type of value
     * @return the newly constructed Expect object or empty Expect object
     */
    @NonNull
    @Contract(value = "!null -> new", pure = true)
    public static <T, X extends Throwable> Expect<T, X> ofNullable(@Nullable T value) {
        return value == null ? empty() : of(value);
    }

    /**
     * Constructs an Expect object wrapping a non-{@code null} throwable object.
     *
     * @param throwable the throwable object to wrap
     * @param <T>       the expected return type
     * @return the newly constructed Expect object
     * @throws NullPointerException if the supplied throwable is {@code null}.
     */
    @NonNull
    @Contract(value = "_ -> new", pure = true)
    public static <T, X extends Throwable> Expect<T, X> failed(@NonNull X throwable) {
        return new Expect<>(null, Objects.requireNonNull(throwable));
    }

    /**
     * Executes the supplied non-{@code null} task, catching any throwable object.
     *
     * @param task the task to execute
     * @param <T>  the result type
     * @return an Expect object wrapping the task's result
     * @throws NullPointerException if the supplied task object is {@code null}
     */
    @NonNull
    @Contract(value = "_ -> new")
    public static <T, X extends Throwable> Expect<T, X> compute(@NonNull Func<T, X> task) {
        Objects.requireNonNull(task);

        try {
            return ofNullable(task.execute());
        } catch (Throwable t) {
            return dirtyFailed(t);
        }
    }

    @SneakyThrows
    private static <T, X extends Throwable> Expect<T, X> dirtyFailed(@NonNull Throwable throwable) {
        try {
            return new Expect<>(null, (X) Objects.requireNonNull(throwable));
        } catch (ClassCastException e) {
            throw throwable;
        }
    }

    /**
     * Executes the supplied non-{@code null} task, catching any thrown exception.
     *
     * @param task the task to execute
     * @return an empty Expect object on success, or an Expect object wrapping the thrown object on failure
     * @throws NullPointerException if the supplied task object is {@code null}
     */
    @NonNull
    @Contract(value = "_ -> new")
    public static <X extends Throwable> Expect<Void, X> compute(@NonNull Task<X> task) {
        Objects.requireNonNull(task);

        try {
            task.execute();
        } catch (Throwable t) {
            return dirtyFailed(t);
        }
        return empty();
    }

    private final T value;
    private final X throwable;

    private Expect(T val, X t) {
        this.value = val;
        this.throwable = t;
    }

    /**
     * Returns an optional wrapping the contained value if no throwable object is present.
     *
     * @return an optional wrapping the contained value
     */
    @NonNull
    @Contract(pure = true)
    public Optional<T> toOptional() {
        return Optional.ofNullable(value);
    }

    public <XX extends Throwable> Expect<T, XX> toOther() {
        return ofNullable(value);
    }

    /**
     * Checks if a value is present.
     *
     * @return {@code true} if a value is present, {@code false} otherwise
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * Checks if no value is present.
     *
     * @return {@code true} if no value is present, {@code false} otherwise
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * Checks if a throwable object is present.
     *
     * @return {@code true} if a throwable object is present, {@code false} otherwise
     */
    public boolean wasFailure() {
        return throwable != null;
    }

    /**
     * Checks if no throwable object is present.
     *
     * @return {@code true} if no throwable object is present, {@code false} otherwise
     */
    public boolean wasSuccess() {
        return throwable == null;
    }

    /**
     * Returns an optional wrapping the contained throwable object.
     *
     * @return an optional wrapping the contained throwable object
     */
    @NonNull
    @Contract(pure = true)
    public Expect<Throwable, ?> getThrowable() {
        return Expect.ofNullable(throwable);
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
     * Throws the caught throwable if present.
     */
    public void rethrow() throws X {
        if (wasFailure())
            throw throwable;
    }

    /**
     * Returns the contained value if it is present and throws an exception if no value is present.
     *
     * @return the contained value
     * @throws NoSuchElementException if no throwable object and no value is present
     * @throws IllegalStateException  if a throwable object and no value is present
     */
    @NonNull
    public T orElseThrow() throws NoSuchElementException, IllegalStateException {
        if (isEmpty())
            throw wasSuccess() ? new NoSuchElementException("No value present") : new IllegalStateException("Execution failed", throwable);

        return value;
    }

    /**
     * Returns the contained value if it is present and attempts to rethrow the caught throwable.
     * If this is an empty Expect object a {@link NoSuchElementException} is thrown instead.
     *
     * @return the contained value
     * @throws X if no value is present
     */
    @NonNull
    public T orElseRethrow() throws X, NoSuchElementException {
        if (isEmpty()) {
            if (wasFailure())
                throw throwable;
            throw new NoSuchElementException("No value present");
        }

        return value;
    }

    /**
     * Executes the supplied task if a value is present and catches any throwable object.
     *
     * @param task the task to execute
     * @return an Expect object wrapping a potential caught throwable object
     */
    @NonNull
    public <XX extends Throwable> Expect<Void, XX> ifPresent(@NonNull ParamTask<? super T, XX> task) {
        Objects.requireNonNull(task);

        return isPresent() ? compute(() -> task.execute(value)) : empty();
    }

    /**
     * Executes the supplied task if a value is present, or executes the alternative task if no value is present and catches and throwable object.
     *
     * @param task  the task to execute
     * @param other the alternative task to execute
     * @return an Expect object wrapping a potential caught throwable object
     */
    @NonNull
    public <XX extends Throwable> Expect<Void, XX> ifPresentOrElse(@NonNull ParamTask<? super T, XX> task,
                                                                   @NonNull ParamTask<? super Expect<? super X, ?>, XX> other) {
        Objects.requireNonNull(task);
        Objects.requireNonNull(other);

        return isPresent() ? compute(() -> task.execute(value)) : compute(() -> other.execute(getThrowable()));
    }

    /**
     * Executes the supplied task if no value is present and catches any throwable object.
     *
     * @param task the task to execute if no value is present
     * @return this Except object if a value is present, otherwise another Expect object wrapping a potential caught throwable object or value from the task's execution
     */
    @NonNull
    public <XX extends Throwable> Expect<T, ? extends Throwable> orElse(@NonNull ParamFunc<? super Expect<? super X, ?>, T, XX> task) {
        Objects.requireNonNull(task);

        return isPresent() ? this : compute(() -> task.execute(getThrowable()));
    }

    /**
     * Executes the supplied task if a value is present and catches and throwable object.
     *
     * @param mapper the task to execute if a value is present
     * @param <U>    the return type
     * @return an empty Expect object if no value is present, otherwise another Expect object wrapping a potential caught throwable object or value from the task's execution
     */
    public <U, XX extends Throwable> Expect<U, XX> map(@NonNull ParamFunc<? super T, U, XX> mapper) {
        Objects.requireNonNull(mapper);

        return isPresent() ? compute(() -> mapper.execute(value)) : empty();
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, throwable);
    }
}
