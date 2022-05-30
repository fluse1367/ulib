package eu.software4you.ulib.core.database.sql;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Representation of sqlite type databases.
 */
public interface SQLiteDatabase extends SqlDatabase {
    /**
     * Returns the path of the sqlite database file.
     *
     * @return the path of the sqlite database file
     */
    @NotNull
    Path getPath();
}
