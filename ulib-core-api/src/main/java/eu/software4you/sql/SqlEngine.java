package eu.software4you.sql;

import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;

import java.io.File;
import java.sql.*;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A Wrapper for a SQL connection
 */
public class SqlEngine {
    private final LinkedHashMap<String, SqlTable> defaultTables = new LinkedHashMap<>();
    public boolean disableAutomaticParameterizedQueries = false;
    private ConnectionData connectionData = new ConnectionData();
    private Driver driver;
    private Connection conn = null;

    /**
     * Default constructor. You still have to set the connection data.
     *
     * @see #SqlEngine(ConnectionData)
     * @see #setConnectionData(ConnectionData)
     */
    public SqlEngine() {
    }

    /**
     * Constructor with faster way of setting the connection data.
     *
     * @param connectionData the connection information
     * @see #setConnectionData(ConnectionData)
     */
    public SqlEngine(ConnectionData connectionData) {
        this.connectionData = connectionData;
    }

    /**
     * Sets the connection information
     *
     * @param data the connection information
     */
    public void setConnectionData(ConnectionData data) {
        connectionData = data;
    }

    /**
     * Checks if the engine is connected to the database
     *
     * @return true if the engine is connected, otherwise false
     */
    public boolean isConnected() {
        try {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Calls {@link #connect(boolean)} with false
     *
     * @throws ClassNotFoundException if Driver class cannot be located
     * @throws IllegalAccessException if the Driver class or its nullary
     *                                constructor is not accessible.
     * @throws InstantiationException if the Driver class represents an abstract class,
     *                                an interface, an array class, a primitive type, or void;
     *                                or if the class has no nullary constructor;
     *                                or if the instantiation fails for some other reason.
     * @throws SQLException           if a database access error occurs or the url
     *                                is {@code null}
     */
    public void connect() throws SQLException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        connect(false);
    }

    /**
     * Creates a connection to the database
     *
     * @param createDefaultTables if the default tables will be created
     * @throws ClassNotFoundException if Driver class cannot be located
     * @throws IllegalAccessException if the Driver class or its nullary
     *                                constructor is not accessible.
     * @throws InstantiationException if the Driver class represents an abstract class,
     *                                an interface, an array class, a primitive type, or void;
     *                                or if the class has no nullary constructor;
     *                                or if the instantiation fails for some other reason.
     * @throws SQLException           if a database access error occurs or the url
     *                                is {@code null}
     */
    public void connect(boolean createDefaultTables) throws SQLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        createConnection();
        if (createDefaultTables)
            createDefaultTables();
    }

    /**
     * Calls {@link #disconnect(boolean)} with false
     *
     * @throws SQLException if a database access error occurs
     */
    public void disconnect() throws SQLException {
        disconnect(false);
    }

    /**
     * Disconnects from the database
     *
     * @param reset if the engine will be resetted
     * @throws SQLException if a database access error occurs
     */
    public void disconnect(boolean reset) throws SQLException {
        destroyConnection();

        if (reset) {
            connectionData = null;
            conn = null;
            defaultTables.clear();
        }

    }

    /**
     * Executes a sql update
     *
     * @param exec the query to execute
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    public void execute(String exec) throws SQLException {
        ULib.logger().finer(() -> "[" + this + "] SQL EXECUTION -> " + exec);
        PreparedStatement ps = conn.prepareStatement(exec);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Executes a parameterized sql update. Does not work with {@link Driver#SqLite}!
     *
     * @param exec   the query to execute
     * @param params the parameters
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    public void execute(String exec, Object... params) throws SQLException {
        ULib.logger().finer(() -> "[" + this + "] SQL EXECUTION -> " + exec);
        StringBuilder paramsString = new StringBuilder();
        for (Object param : params) {
            paramsString.append(param).append(" ");
        }
        ULib.logger().finer(() -> "[" + this + "] SQL EXECUTION PARAMS -> " + paramsString);
        PreparedStatement ps = conn.prepareStatement(exec);
        for (int i = 1; i <= params.length; i++) {
            ps.setObject(i, params[i - 1]);
        }
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Executes a sql query
     *
     * @param query the query to execute
     * @return the result of the query
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed <code>Statement</code>, the given
     *                      SQL statement produces anything other than a single
     *                      <code>ResultSet</code> object, the method is called on a
     *                      <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    public ResultSet query(String query) throws SQLException {
        ULib.logger().finer(() -> "[" + this + "] SQL QUERY -> " + query);
        return conn.createStatement().executeQuery(query);
    }

    /**
     * Executes a parameterized sql query. Does not work with {@link Driver#SqLite}!
     *
     * @param query  the query to execute
     * @param params the parameters
     * @return the result of the query
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed <code>Statement</code>, the given
     *                      SQL statement produces anything other than a single
     *                      <code>ResultSet</code> object, the method is called on a
     *                      <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    public ResultSet query(String query, Object... params) throws SQLException {
        ULib.logger().finer(() -> "[" + this + "] SQL QUERY -> " + query);
        PreparedStatement ps = conn.prepareStatement(query);
        for (int i = 1; i <= params.length; i++) {
            ps.setObject(i, params[i - 1]);
        }
        return ps.executeQuery();
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        return conn.prepareStatement(query);
    }

    /**
     * Gets a unmodifiable map with the default tables in it
     *
     * @return the unmodifiable map
     */
    public Map<String, SqlTable> getDefaultTables() {
        return Collections.unmodifiableMap(defaultTables);
    }

    /**
     * Adds a {@link SqlTable} to the default tables
     *
     * @param table the Table to be added
     */
    public void addDefaultTable(SqlTable table) {
        if (defaultTables.containsValue(table.name()))
            throw new IllegalArgumentException("Table with this name already added!");
        defaultTables.put(table.name(), table);
    }

    /**
     * Gets the table from the default tables with the specified name
     *
     * @param name the name of the table
     * @return the table with the specified name or {@code null}, if it does not exist
     */
    public SqlTable getDefaultTable(String name) {
        return defaultTables.get(name);
    }

    /**
     * Creates a new Table instance
     *
     * @param name the name of the table
     * @return the table
     */
    public SqlTable newTable(String name) {
        return new SqlTable(this, name);
    }

    private void createConnection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        this.driver = connectionData.file != null ? Driver.SqLite : Driver.MySQL;
        createConnection(this.driver);
    }

    private void createConnection(Driver driver) throws ClassNotFoundException, IllegalAccessException, InstantiationException, SQLException {
        DriverLoader.load(driver);

        if (driver == Driver.SqLite)
            disableAutomaticParameterizedQueries = true;

        if (driver.equals(Driver.SqLite) && connectionData.file != null) {
            conn = DriverManager.getConnection(driver.url + connectionData.file.getPath());
        } else if (driver.equals(Driver.MySQL) && connectionData.host != null && connectionData.database != null && connectionData.user != null && connectionData.password != null) {
            conn = DriverManager.getConnection(driver.url + connectionData.host + ":" + connectionData.port + "/"
                                               + connectionData.database + "?autoReconnect=true&maxReconnects=5", connectionData.user, connectionData.password);
        }
    }

    private void destroyConnection() throws SQLException {
        if (isConnected())
            conn.close();
    }

    private void createDefaultTables() throws SQLException {
        for (SqlTable table : defaultTables.values()) {
            table.create();
        }
    }

    @Override
    public String toString() {
        return getClass().getName() + "/Driver=" + driver.toString() + "/ConnectionData=" + connectionData.toString() + "/Connected=" + isConnected();
    }

    /**
     * Representation for different database type drivers.
     */
    public enum Driver {
        /**
         * The MySQL JDBC Driver
         */
        MySQL("jdbc:mysql://"),
        /**
         * The sqlite JDBC Driver
         */
        SqLite("jdbc:sqlite:"),
        ;

        private final String url;

        Driver(String url) {
            this.url = url;
        }
    }

    /* helper to load the driver libraries only when necessary */
    public abstract static class DriverLoader {
        @Await
        private static DriverLoader loader;

        private static void load(Driver driver) {
            loader.load0(driver);
        }

        protected abstract void load0(Driver driver);
    }

    /**
     * Representation of either the authentication credentials or other data needed in order to connect
     */
    public static class ConnectionData {
        private int port = 3306;
        private String host = null;
        private File file = null;
        private String user = null;
        private String password = null;
        private String database = null;

        /**
         * Connection information to MySQL server
         *
         * @param host     the host of mysql server
         * @param user     the user
         * @param password the password for given user
         * @param database the target database
         */
        public ConnectionData(String host, String user, String password, String database) {
            this.host = host;
            this.user = user;
            this.password = password;
            this.database = database;
        }

        /**
         * Connection information to MySQL server
         *
         * @param host     the host of mysql server
         * @param port     the port of mysql server
         * @param user     the user
         * @param password the password for given user
         * @param database the target database
         */
        public ConnectionData(String host, int port, String user, String password, String database) {
            this.host = host;
            this.port = port;
            this.user = user;
            this.password = password;
            this.database = database;
        }

        /**
         * Connection information to sqlite file
         *
         * @param file the sqlite file. Will be created
         *             if not exists
         */
        public ConnectionData(File file) {
            this.file = file;
        }

        private ConnectionData() {
        }

        @Override
        public String toString() {
            return file == null ? "host=" + host + "/database=" + database + "/user=" + user + "/password=" + password : "file=" + file.getPath();
        }
    }
}
