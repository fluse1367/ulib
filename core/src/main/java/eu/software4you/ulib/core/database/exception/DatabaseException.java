package eu.software4you.ulib.core.database.exception;

import eu.software4you.ulib.core.database.Database;

/**
 * Superclass for all {@link Database} related exceptions.
 */
public class DatabaseException extends RuntimeException {
    private final Database database;

    public DatabaseException(Database database) {
        this.database = database;
    }

    public DatabaseException(String message, Database database) {
        super(message);
        this.database = database;
    }

    public DatabaseException(String message, Throwable cause, Database database) {
        super(message, cause);
        this.database = database;
    }

    public DatabaseException(Throwable cause, Database database) {
        super(cause);
        this.database = database;
    }

    public DatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, Database database) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.database = database;
    }
}
