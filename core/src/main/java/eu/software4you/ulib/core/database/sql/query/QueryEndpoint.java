package eu.software4you.ulib.core.database.sql.query;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * The end of a query.
 */
public interface QueryEndpoint {

    /**
     * Builds and executes the query. Expects return data from the database.
     * <p>This method skips certain parameter numbers (but not the values!) if parameters are already known for a specific number.
     *
     * @param parameters the query parameters
     * @return what the database returned
     * @see PreparedStatement#executeQuery()
     * @see PreparedStatement#setObject(int, Object)
     */
    @NotNull
    ResultSet query(@NotNull Object... parameters);

    /**
     * Builds and executes the query.
     * <p>This method skips certain parameter numbers (but not the values!) if parameters are already known for a specific number.
     *
     * @param parameters the query parameters
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @see PreparedStatement#executeUpdate()
     * @see PreparedStatement#setObject(int, Object)
     */
    int update(@NotNull Object... parameters);

    /**
     * Builds the query with potentially added parameters.
     *
     * @return the built query
     */
    @NotNull
    PreparedStatement build();

    /**
     * Builds the raw query string.
     *
     * @return the query string.
     */
    @NotNull
    String buildRawQuery();

    /**
     * Builds the query with parameters.
     * <p>This method skips certain parameter numbers (but not the values!) if parameters are already known for a specific number.
     *
     * @param parameters the parameters
     * @return the built query
     * @see PreparedStatement#setObject(int, Object)
     */
    @NotNull
    PreparedStatement build(@NotNull Object... parameters);

    /**
     * Limits the result set to a specific amount of rows.<br>
     * Only numbers {@code >= 0} will be effective. Once the limit was set, it cannot be set again / changed.<br>
     * No exception will be thrown in case of numbers {@code < 0} or multiple usage.
     *
     * @param limit the amount
     * @return this
     */
    @NotNull
    @Contract("_ -> this")
    QueryEndpoint limit(long limit);
}
