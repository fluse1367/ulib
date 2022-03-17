package eu.software4you.ulib.core.database.sql.query;

import eu.software4you.ulib.core.database.sql.Column;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    SetQuery set(@NotNull Column<?> column, @NotNull Object to);

    /**
     * Sets a column to a specific value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @param column the column to set
     * @return this
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    SetQuery setP(@NotNull Column<?> column);

    /**
     * Sets a column to a specific value using a parameterized sql query.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery setP(@NotNull Column<?> column, @Nullable Object to);

    /**
     * Sets a column to a specific value.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery set(@NotNull String column, @NotNull Object to);

    /**
     * Sets a column to a specific value using a parameterized sql query.
     * <p>The value must be handed over later as parameter in {@link QueryEndpoint#build(Object...)}, {@link QueryEndpoint#query(Object...)} or {@link QueryEndpoint#update(Object...)}.
     *
     * @param column the column to set
     * @return this
     * @see QueryEndpoint#build(Object...)
     * @see QueryEndpoint#query(Object...)
     * @see QueryEndpoint#update(Object...)
     */
    SetQuery setP(@NotNull String column);

    /**
     * Sets a column to a specific value using a parameterized sql query.
     *
     * @param column the column to set
     * @param to     the value
     * @return this
     */
    SetQuery setP(@NotNull String column, @Nullable Object to);
}
