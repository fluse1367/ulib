package eu.software4you.database.sql.query;

import eu.software4you.database.sql.Column;

/**
 * The start of a query, that attempts to set values.
 */
public interface SetQuery extends Query {
    /**
     * Sets a column to a specific value.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery set(Column<?> column, Object to);

    /**
     * Sets a column to a specific value that is handed over later as parameter.
     *
     * @param column the column to set
     * @return this
     */
    SetQuery setP(Column<?> column);

    /**
     * Sets a column to a specific value.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery set(String column, Object to);

    /**
     * Sets a column to a specific value that is handed over later as parameter.
     *
     * @param column the column to set
     * @return this
     */
    SetQuery setP(String column);
}
