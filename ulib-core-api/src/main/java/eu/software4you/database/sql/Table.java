package eu.software4you.database.sql;

import eu.software4you.common.Nameable;
import eu.software4you.common.collection.Pair;
import eu.software4you.database.sql.query.Query;
import eu.software4you.database.sql.query.QueryStart;
import eu.software4you.database.sql.query.SetQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a sql Table.
 */
public interface Table extends Nameable {

    /**
     * Returns the columns this table has.
     *
     * @return the columns
     */
    @NotNull
    Column<?>[] getColumns();

    /**
     * Searches for a column within this table.
     *
     * @param name the column name
     * @return the column instance, or {@code null} if not found
     */
    @Nullable
    Column<?> getColumn(@NotNull String name);

    @Override
    @NotNull String getName();

    /**
     * Attempts to create this table in the database.
     *
     * @return {@code true}, if the operation was successful
     */
    boolean create();

    /**
     * Attempts to delete this table from the database.
     *
     * @return {@code true}, if the operation was successful
     */
    boolean drop();

    /**
     * Checks if this table exists within the database.
     *
     * @return {@code true} if this table exists, {@code false} otherwise
     */
    boolean exists();

    /**
     * Selects data from the table.
     *
     * @param what     the column to select
     * @param whatElse additional columns to select
     * @return the query builder
     */
    @NotNull Query select(@NotNull String what, String... whatElse);

    /**
     * Selects only different values from the table.
     *
     * @param what     the column to select
     * @param whatElse additional columns to select
     * @return the query builder
     */
    @NotNull Query selectDistinct(@NotNull String what, String... whatElse);

    /**
     * Updates the table.
     *
     * @return the query builder
     */
    @NotNull SetQuery update();

    /**
     * Inserts values into the table.<br>
     * The values have to correspond with the columns of the table in the same order.
     *
     * @param value  the value to insert
     * @param values additional values to insert
     * @return {@code true}, if the operation was successful
     */
    boolean insert(@NotNull Object value, Object... values);

    /**
     * Inserts values into the table.
     *
     * @param value  {@link Pair#getFirst()}: the the column in which to insert, {@link Pair#getSecond()}: the value to insert
     * @param values additional values to insert
     * @return {@code true}, if the operation was successful
     */
    // @SafeVarargs
    @SuppressWarnings("unchecked")
    boolean insert(@NotNull Pair<String, Object> value, Pair<String, Object>... values);

    /**
     * Deletes rows from the table.
     *
     * @return the query builder
     */
    QueryStart delete();

    /**
     * Deletes all data inside the table.
     *
     * @return {@code true}, if the operation was successful
     */
    boolean truncate();

}
