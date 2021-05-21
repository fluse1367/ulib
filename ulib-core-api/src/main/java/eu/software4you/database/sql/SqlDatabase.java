package eu.software4you.database.sql;

import eu.software4you.database.Database;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Collection;

/**
 * Representation of sql type databases.
 */
public interface SqlDatabase extends Database {

    /**
     * Retrieves the current connection to the database.
     *
     * @return the connection
     * @throws IllegalStateException when no connection was established yet.
     */
    @NotNull
    Connection getConnection() throws IllegalStateException;

    /**
     * Wraps {@link Connection#prepareStatement(String)} for faster access.
     *
     * @param sql an SQL statement that may contain one or more '?' IN parameter placeholders
     * @return a new default PreparedStatement object containing the pre-compiled SQL statement
     * @throws IllegalStateException when no connection was established yet.
     * @see Connection#prepareStatement(String)
     */
    @SneakyThrows
    @NotNull
    default PreparedStatement prepareStatement(String sql) throws IllegalStateException {
        return getConnection().prepareStatement(sql);
    }

    /**
     * Wraps {@link Connection#prepareCall(String)} for faster access.
     *
     * @param sql an SQL statement that may contain one or more '?' parameter placeholders.
     *            Typically this statement is specified using JDBC call escape syntax.
     * @return a new default PreparedStatement object containing the pre-compiled SQL statement
     * @throws IllegalStateException when no connection was established yet.
     * @see Connection#prepareStatement(String)
     */
    @SneakyThrows
    @NotNull
    default CallableStatement prepareCall(String sql) throws IllegalStateException {
        return getConnection().prepareCall(sql);
    }

    /**
     * Returns this database's tables (the wrapper knows of).
     *
     * @return the tables
     */
    @NotNull
    Collection<Table> getTables();

    /**
     * Searches for a table within the database.
     *
     * @param name the table name
     * @return the table instance, or {@code null} if not found
     */
    @Nullable
    Table getTable(String name);

    /**
     * Adds a new table to this wrapper.
     *
     * @param name    the name
     * @param column  the 1st column
     * @param columns other columns
     * @return the table
     * @see ColumnBuilder
     */
    @NotNull
    Table addTable(String name, Column<?> column, Column<?>... columns);

    /**
     * Adds a new table to this wrapper.
     *
     * @param name     the name
     * @param builder  the 1st column
     * @param builders other columns
     * @return the table
     * @see ColumnBuilder
     */
    @NotNull
    Table addTable(String name, ColumnBuilder<?> builder, ColumnBuilder<?>... builders);
}
