package eu.software4you.ulib.core.api.database.sql.query;

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
    ResultSet query(Object... parameters);

    /**
     * Builds and executes the query.
     * <p>This method skips certain parameter numbers (but not the values!) if parameters are already known for a specific number.
     *
     * @param parameters the query parameters
     * @return either (1) the row count for SQL Data Manipulation Language (DML) statements or (2) 0 for SQL statements that return nothing
     * @see PreparedStatement#executeUpdate()
     * @see PreparedStatement#setObject(int, Object)
     */
    int update(Object... parameters);

    /**
     * Builds the query with potentially added parameters.
     *
     * @return the built query
     */
    PreparedStatement build();

    /**
     * Builds the raw query string.
     *
     * @return the query string.
     */
    String buildRawQuery();

    /**
     * Builds the query with parameters.
     * <p>This method skips certain parameter numbers (but not the values!) if parameters are already known for a specific number.
     *
     * @param parameters the parameters
     * @return the built query
     * @see PreparedStatement#setObject(int, Object)
     */
    PreparedStatement build(Object... parameters);

    /**
     * Limits the result set to a specific amount of rows.<br>
     * Only numbers {@code >= 0} will be effective. Once the limit was set, it cannot be set again / changed.<br>
     * No exception will be thrown in case of numbers {@code < 0} or multiple usage.
     *
     * @param limit the amount
     * @return this
     */
    QueryEndpoint limit(long limit);
}
