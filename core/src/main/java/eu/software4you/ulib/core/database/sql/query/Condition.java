package eu.software4you.ulib.core.database.sql.query;

import eu.software4you.ulib.core.database.sql.Column;
import org.jetbrains.annotations.*;

/**
 * Represents a condition that is to be completed.
 *
 * @param <R> the return value at completion
 */
public interface Condition<R> {
    /**
     * Negates the condition.
     *
     * @return this
     */
    @NotNull
    @Contract("-> this")
    Condition<R> not();

    /**
     * Tests for equality.
     *
     * @param what the value to test for
     * @return R
     */
    @NotNull
    R isEqualTo(@Nullable Object what);

    /**
     * Tests for equality with a specific value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isEqualToP();

    /**
     * Tests for equality with a specific value using a parameterized sql query.
     *
     * @param what the value to set for
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isEqualToP(@Nullable Object what);

    /**
     * Tests if a value is greater than another value.
     *
     * @param than another value
     * @return R
     */
    @NotNull
    R isGreaterThan(@NotNull Object than);

    /**
     * Tests if a value is greater than another value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isGreaterThanP();

    /**
     * Tests if a value is greater than another value using a parameterized sql query.
     *
     * @param than another value
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isGreaterThanP(@NotNull Object than);

    /**
     * Tests if a value is greater than or equals another value.
     *
     * @param than another value
     * @return R
     */
    @NotNull
    R isGreaterOrEquals(@NotNull Object than);

    /**
     * Tests if a value is greater than or equals another value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isGreaterOrEqualsP();

    /**
     * Tests if a value is greater than or equals another value using a parameterized sql query.
     *
     * @param than another value
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isGreaterOrEqualsP(@NotNull Object than);

    /**
     * Tests if a value is less than another value.
     *
     * @param than another value
     * @return R
     */
    @NotNull
    R isLessThan(@NotNull Object than);

    /**
     * Tests if a value is less than another value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isLessThanP();

    /**
     * Tests if a value is less than another value using a parameterized sql query.
     *
     * @param than another value
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isLessThanP(@NotNull Object than);

    /**
     * Tests if a value is less than or equals another value.
     *
     * @param than another value
     * @return R
     */
    @NotNull
    R isLessOrEquals(@NotNull Object than);

    /**
     * Tests if a value is less than or equals another value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isLessOrEqualsP();

    /**
     * Tests if a value is less than or equals another value using a parameterized sql query.
     *
     * @param than another value
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isLessOrEqualsP(@NotNull Object than);

    /**
     * Tests if a value is between two other values.
     *
     * @param a the first value
     * @param b the second value
     * @return R
     */
    @NotNull
    R isBetween(@NotNull Object a, @NotNull Object b);

    /**
     * Tests if a value is between two other values using a parameterized sql query.
     * <p>The two others values must be handed over later as parameters in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isBetweenP();

    /**
     * Tests if a value is between two other values using a parameterized sql query.
     *
     * @param a the first value
     * @param b the second value
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isBetweenP(@NotNull Object a, @NotNull Object b);

    /**
     * Tests if a value matches a specific pattern.
     *
     * @param pattern the pattern
     * @return R
     */
    @NotNull
    R isLike(@NotNull String pattern);

    /**
     * Tests if a value matches a specific pattern using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isLikeP();

    /**
     * Tests if a value matches a specific pattern using a parameterized sql query.
     *
     * @param pattern the pattern
     * @return R
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isLikeP(@NotNull String pattern);

    /**
     * Tests if a value is within a pool of other values.
     *
     * @param val  the value to test
     * @param vals additional values to test for
     * @return R
     * @see Where#or(String)
     * @see Where#or(Column)
     * @see Where#orRaw(String)
     */
    @NotNull
    R isIn(@NotNull Object val, @NotNull Object... vals);

    /**
     * Tests if a value is within a pool of other values using a parameterized sql query.
     * <p>The others values must be handed over later as parameters in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @param amount the amount of other values
     * @return R
     * @see Where#or(String)
     * @see Where#or(Column)
     * @see Where#orRaw(String)
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isInP(int amount);

    /**
     * Tests if a value is within a pool of other values using a parameterized sql query.
     *
     * @param val  the value to test
     * @param vals additional values to test for
     * @return R
     * @see Where#or(String)
     * @see Where#or(Column)
     * @see Where#orRaw(String)
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    @NotNull
    R isInP(@NotNull Object val, @NotNull Object... vals);
}
