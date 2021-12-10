package eu.software4you.ulib.core.api.database.sql.query;

import eu.software4you.ulib.core.api.database.sql.Column;
import org.jetbrains.annotations.NotNull;

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
    Condition<Where> and(@NotNull Column<?> column);

    /**
     * Appends another condition that needs to apply too.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> and(@NotNull String column);

    /**
     * Appends another condition that needs to apply too.
     *
     * @param condition the whole condition string, e.g. "column_1 = 0"
     * @return this
     */
    Where andRaw(@NotNull String condition);

    /**
     * Appends another condition that needs to apply alternatively.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> or(@NotNull Column<?> column);

    /**
     * Appends another condition that needs to apply alternatively.
     *
     * @param column the column
     * @return the condition builder to complete the condition
     */
    Condition<Where> or(@NotNull String column);

    /**
     * Appends another condition that needs to apply alternatively.
     *
     * @param condition the whole condition string, e.g. "column_1 = 0"
     * @return this
     */
    Where orRaw(@NotNull String condition);
}
