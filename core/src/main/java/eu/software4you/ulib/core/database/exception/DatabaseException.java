package eu.software4you.ulib.core.database.exception;

import eu.software4you.ulib.core.database.Database;
import org.jetbrains.annotations.NotNull;

/**
 * Superclass for all {@link Database} related exceptions.
 */
public class DatabaseException extends RuntimeException {
    private final Database database;

    public DatabaseException(@NotNull Database database) {
        this.database = database;
    }

    public DatabaseException(@NotNull String message, @NotNull Database database) {
        super(message);
        this.database = database;
    }

    public DatabaseException(@NotNull String message, @NotNull Throwable cause, @NotNull Database database) {
        super(message, cause);
        this.database = database;
    }

    public DatabaseException(@NotNull Throwable cause, @NotNull Database database) {
        super(cause);
        this.database = database;
    }

    public DatabaseException(@NotNull String message, @NotNull Throwable cause, boolean enableSuppression, boolean writableStackTrace, @NotNull Database database) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.database = database;
    }
}
