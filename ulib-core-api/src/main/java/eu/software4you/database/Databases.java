package eu.software4you.database;

import eu.software4you.database.sql.MySQLDatabase;
import eu.software4you.database.sql.SQLiteDatabase;
import eu.software4you.database.sql.SqlDatabase;
import eu.software4you.ulib.Await;
import lombok.val;
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
    @Await
    private static Databases impl;

    /**
     * Wraps a already existing connection.
     *
     * @param connection the connection to be wrapped
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    public static SqlDatabase wrap(Connection connection) throws IllegalArgumentException {
        return impl.wrap0(connection);
    }

    /**
     * Prepares a wrapper.
     *
     * @param url the database url
     * @return the instance
     * @throws IllegalArgumentException when no suitable wrapper for the used protocol is found.
     */
    @NotNull
    public static Database prepare(String url) {
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
    public static Database prepare(String url, Properties info) {
        return impl.prepare0(url, info);
    }

    /**
     * Prepares a new SQLite {@link Connection} and wraps it.
     *
     * @param file the SQLite file
     * @return the instance
     * @see DriverManager#getConnection(String)
     */
    @NotNull
    public static SQLiteDatabase prepare(File file) {
        return impl.prepare0(file);
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
    public static MySQLDatabase prepare(String host, int port, String database, String user, String password, String... parameters) {
        return impl.prepare0(host, port, database, user, password, parameters);
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
    public static Database connect(String url, Properties info) throws IllegalArgumentException {
        val db = prepare(url, info);
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
    public static SQLiteDatabase connect(File file) {
        val db = prepare(file);
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
    public static MySQLDatabase connect(String host, int port, String database, String user, String password, String... parameters) {
        val db = prepare(host, port, database, user, password, parameters);
        db.connect();
        return db;
    }

    protected abstract Database prepare0(String url, Properties info);

    protected abstract SqlDatabase wrap0(Connection connection);

    protected abstract SQLiteDatabase prepare0(File file);

    protected abstract MySQLDatabase prepare0(String host, int port, String database, String user, String password, String... parameters);

}