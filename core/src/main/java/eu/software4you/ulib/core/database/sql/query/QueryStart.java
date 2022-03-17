package eu.software4you.ulib.core.database.sql.query;

import eu.software4you.ulib.core.database.sql.Column;
import org.jetbrains.annotations.NotNull;

/**
 * The start of a query that requires additional limitation.
 */
public interface QueryStart {
    /**
     * Limits the query using the sql {@code WHERE} keyword.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> where(@NotNull Column<?> column);

    /**
     * Limits the query using the sql {@code WHERE} keyword.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> where(@NotNull String column);

    /**
     * Limits the query using the sql {@code WHERE} keyword.
     *
     * @param condition the whole condition string, e.g. "column_1 = 0"
     * @return this
     */
    Where whereRaw(@NotNull String condition);
}
