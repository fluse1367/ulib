package eu.software4you.database.sql;

import java.nio.file.Path;

/**
 * Representation of sqlite type databases.
 */
public interface SQLiteDatabase extends SqlDatabase {
    // TODO

    Path getPath();
}
