package eu.software4you.ulib.core.api.database;

/**
 * Very basic functions all databases share.
 */
public interface Database {
    /**
     * Checks if a connection to the database exists.
     *
     * @return {@code true} if the database is connected, {@code false} otherwise.
     */
    boolean isConnected();

    /**
     * Attempts to create a connection to the database.
     *
     * @throws IllegalStateException when attempting to re-create a already existing connection.
     */
    void connect() throws IllegalStateException;

    /**
     * Attempts to close a existing connection to the database.
     *
     * @throws IllegalStateException when a attempting to close a not existing connection.
     */
    void disconnect() throws IllegalStateException;
}
