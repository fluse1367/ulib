package eu.software4you.ulib.core.database.sql;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.common.Keyable;
import eu.software4you.ulib.core.database.sql.query.*;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Optional;

/**
 * Represents a sql Table.
 */
public interface Table extends Keyable<String> {

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
    @NotNull
    Optional<Column<?>> getColumn(@NotNull String name);

    @NotNull
    String getName();

    @Override
    @NotNull
    default String getKey() {
        return getName();
    }

    /**
     * Attempts to create this table in the database.
     *
     * @return {@code true}, if the operation was successful
     */
    @NotNull
    Expect<Void, SQLException> create();

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
    @NotNull Query select(@NotNull String what, @NotNull String... whatElse);

    /**
     * Selects only different values from the table.
     *
     * @param what     the column to select
     * @param whatElse additional columns to select
     * @return the query builder
     */
    @NotNull Query selectDistinct(@NotNull String what, @NotNull String... whatElse);

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
    boolean insert(@NotNull Object value, @NotNull Object... values);

    /**
     * Inserts values into the table.
     *
     * @param value  {@link Pair#getFirst()}: the the column in which to insert, {@link Pair#getSecond()}: the value to insert
     * @param values additional values to insert
     * @return {@code true}, if the operation was successful
     */
    // @SafeVarargs
    @SuppressWarnings("unchecked")
    boolean insert(@NotNull Pair<String, Object> value, @NotNull Pair<String, Object>... values);

    /**
     * Deletes rows from the table.
     *
     * @return the query builder
     */
    @NotNull
    QueryStart delete();

    /**
     * Deletes all data inside the table.
     *
     * @return {@code true}, if the operation was successful
     */
    boolean truncate();

}
