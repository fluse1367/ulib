package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

/**
 * The start of a query, that attempts to set values.
 */
public interface SetQuery extends Query {
    /**
     * Can be used as value to indicate a parameter instead of the actual value.
     *
     * @see QueryEndpoint#build(Object...)
     * @see java.sql.PreparedStatement#setObject(int, Object)
     */
    String VALUE_PARAMETER = "?";

    /**
     * Sets a column to a specific value.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery set(Column<?> column, Object to);

    /**
     * Sets a column to a specific value.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery set(String column, Object to);
}
