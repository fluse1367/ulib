package eu.software4you.ulib.core.api.database;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.database.sql.MySQLDatabase;
import eu.software4you.ulib.core.api.database.sql.SQLiteDatabase;
import eu.software4you.ulib.core.api.database.sql.SqlDatabase;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Access point for all database related classes.
 */
public abstract class Databases {
    private static Databases impl() {
        return ULib.service(Databases.class);
    }

    /**
     * Wraps a already existing connection.
     *
     * @param connection the connection to be wrapped
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    public static SqlDatabase wrap(@NotNull Connection connection) throws IllegalArgumentException {
        return impl().wrap0(connection);
    }

    /**
     * Prepares a wrapper.
     *
     * @param url the database url
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    public static Database prepare(@NotNull String url) {
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
    public static Database prepare(@NotNull String url, @NotNull Properties info) {
        return impl().prepare0(url, info);
    }

    /**
     * Prepares a new SQLite {@link Connection} and wraps it.
     *
     * @param file the SQLite file
     * @return the instance
     * @see DriverManager#getConnection(String)
     */
    @NotNull
    public static SQLiteDatabase prepare(@NotNull File file) {
        return impl().prepare0(file);
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
    public static MySQLDatabase prepare(@NotNull String host, int port, @NotNull String database, @NotNull String user, @NotNull String password, String... parameters) {
        return impl().prepare0(host, port, database, user, password, parameters);
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
    public static Database connect(@NotNull String url, @NotNull Properties info) throws IllegalArgumentException {
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
    public static SQLiteDatabase connect(@NotNull File file) {
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
    public static MySQLDatabase connect(@NotNull String host, int port, @NotNull String database, @NotNull String user, @NotNull String password, String... parameters) {
        var db = prepare(host, port, database, user, password, parameters);
        db.connect();
        return db;
    }

    protected abstract Database prepare0(String url, Properties info);

    protected abstract SqlDatabase wrap0(Connection connection);

    protected abstract SQLiteDatabase prepare0(File file);

    protected abstract MySQLDatabase prepare0(String host, int port, String database, String user, String password, String... parameters);

}
