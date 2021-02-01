package eu.software4you.database.sql;

import eu.software4you.common.Nameable;
import eu.software4you.database.sql.query.Query;
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
    Column<?> getColumn(String name);

    @Override
    @NotNull String getName();

    /**
     * Attempts to create this table in the database.
     */
    void create();

    /**
     * Attempts to delete this table from the database.
     */
    void drop();

    /**
     * Checks if this table exists within the database.
     *
     * @return {@code true} if this table exists, {@code false} otherwise
     */
    boolean exists();

    /**
     * @deprecated not implemented
     */
    @Deprecated
    Query select(String what, String... select);

    /**
     * @deprecated not implemented
     */
    @Deprecated
    Query selectDistinct(String what, String... select);

    /**
     * @deprecated not implemented
     */
    @Deprecated
    SetQuery update();

}
