package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

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
    Condition<R> not();

    /**
     * Tests for equality.
     *
     * @param what the value
     * @return R
     */
    R isEqualTo(Object what);

    /**
     * Tests if a value is greater than another value.
     *
     * @param than another value
     * @return R
     */
    R isGreaterThan(Object than);

    /**
     * Tests if a value is greater than or equals another value.
     *
     * @param than another value
     * @return R
     */
    R isGreaterOrEquals(Object than);

    /**
     * Tests if a value is less than another value.
     *
     * @param than another value
     * @return R
     */
    R isLessThan(Object than);

    /**
     * Tests if a value is less than or equals another value.
     *
     * @param than another value
     * @return R
     */
    R isLessOrEquals(Object than);

    /**
     * Tests if a value is between two other values.
     *
     * @param a the first value
     * @param b the second value
     * @return R
     */
    R isBetween(Object a, Object b);

    /**
     * Tests if a value matches a specific pattern.
     *
     * @param pattern the pattern
     * @return R
     */
    R isLike(String pattern);

    /**
     * Short for multiple or conditions.
     *
     * @param val  the value to test
     * @param vals additional values
     * @return R
     * @see Where#or(String)
     * @see Where#or(Column)
     * @see Where#orRaw(String)
     */
    R isIn(Object val, Object... vals);
}
