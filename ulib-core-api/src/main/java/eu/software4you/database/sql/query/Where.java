package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

/**
 * Continuation of limitations using the sql {@code WHERE} keyword.
 */
public interface Where extends QueryEndpoint {
    /**
     * Appends another condition that needs to apply too.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> and(Column<?> column);

    /**
     * Appends another condition that needs to apply too.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> and(String column);

    /**
     * Appends another condition that needs to apply too.
     *
     * @param condition the whole condition string, e.g. "column_1 = 0"
     * @return this
     */
    Where andRaw(String condition);

    /**
     * Appends another condition that needs to apply alternatively.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> or(Column<?> column);

    /**
     * Appends another condition that needs to apply alternatively.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> or(String column);

    /**
     * Appends another condition that needs to apply alternatively.
     *
     * @param condition the whole condition string, e.g. "column_1 = 0"
     * @return this
     */
    Where orRaw(String condition);
}
