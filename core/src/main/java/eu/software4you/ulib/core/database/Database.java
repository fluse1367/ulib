package eu.software4you.ulib.core.database;

import eu.software4you.ulib.core.database.sql.*;
import eu.software4you.ulib.core.impl.database.Databases;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Very basic functions all databases share.
 */
public interface Database {

    /**
     * Wraps a already existing connection.
     *
     * @param connection the connection to be wrapped
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    static SqlDatabase wrap(@NotNull Connection connection) {
        return Databases.wrap(connection);
    }

    /**
     * Prepares a wrapper.
     *
     * @param url the database url
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    static Database prepare(@NotNull String url) {
        return prepare(url, new Properties());
    }

    /**
     * Prepares a wrapper.
     *
     * @param url  the database url
     * @param info the connection info
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    static Database prepare(@NotNull String url, @NotNull Properties info) {
        return Databases.prepare(url, info);
    }

    /**
     * Prepares a new SQLite {@link Connection} and wraps it.
     *
     * @param file the SQLite file
     * @return the instance
     * @see DriverManager#getConnection(String)
     */
    @NotNull
    static SQLiteDatabase prepare(@NotNull File file) {
        return Databases.prepare(file);
    }

    /**
     * Prepares a new MySQL {@link Connection} and wraps it.
     *
     * @param host       the host
     * @param port       the port (usually 3306)
     * @param database   the database
     * @param user       the user
     * @param password   the password
     * @param parameters optional URL parameters (e.g. "param1=val1", "param2=val2"). Will <b>not</b> be encoded.
     * @return the instance
     * @see DriverManager#getConnection(String, String, String)
     * @see URLEncoder#encode(String, String)
     */
    @NotNull
    static MySQLDatabase prepare(@NotNull String host, int port, @NotNull String database, @NotNull String user, @NotNull String password, @NotNull String... parameters) {
        return Databases.prepare(host, port, database, user, password, parameters);
    }

    /**
     * Creates a new Database connection and wraps it.
     *
     * @param url  the database url
     * @param info the connection arguments, usually at least a {@code user} and a {@code password}
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     * @see DriverManager#getConnection(String, Properties)
     */
    @NotNull
    static Database connect(@NotNull String url, @NotNull Properties info) throws IllegalArgumentException {
        var db = prepare(url, info);
        db.connect();
        return db;
    }

    /**
     * Creates a new SQLite {@link Connection} and wraps it.
     *
     * @param file the SQLite file
     * @return the instance
     * @see DriverManager#getConnection(String)
     */
    @NotNull
    static SQLiteDatabase connect(@NotNull File file) {
        var db = prepare(file);
        db.connect();
        return db;
    }

    /**
     * Creates a new MySQL {@link Connection} and wraps it.
     *
     * @param host       the host
     * @param port       the port (usually 3306)
     * @param database   the database
     * @param user       the user
     * @param password   the password
     * @param parameters optional URL parameters (e.g. "param1=val1", "param2=val2"). Will <b>not</b> be encoded.
     * @return the instance
     * @see DriverManager#getConnection(String, String, String)
     * @see URLEncoder#encode(String, String)
     */
    @NotNull
    static MySQLDatabase connect(@NotNull String host, int port, @NotNull String database, @NotNull String user, @NotNull String password, @NotNull String... parameters) {
        var db = prepare(host, port, database, user, password, parameters);
        db.connect();
        return db;
    }

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
