package eu.software4you.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * A wrapper to access a table in a SQL database.
 * For each operation a {@code keyVal} is needed. This parameter will be used to detect the correct entry in the table.
 * The entry detected will be that entry where the {@code key} column value equals {@code keyVal}. See the constructor.
 * So this wrapper is for a <b>multiple</b> table entries.
 *
 * @param <V> the type of the detecting key
 * @see #SqlTableWrapper(SqlEngine, SqlTable, String)
 */
public class SqlTableWrapper<V> {
    private final SqlEngine sqlEngine;
    private final SqlTable table;
    private final String key;

    /**
     * @param sqlEngine the sqlEngine connection
     * @param table     the table that will be used
     * @param key       the column that is used to detect an entry in the table with a {@code keyVal}
     */
    public SqlTableWrapper(SqlEngine sqlEngine, SqlTable table, String key) {
        this.sqlEngine = sqlEngine;
        this.table = table;
        this.key = key;
    }

    /**
     * Gets the {@link SqlEngine} instance
     *
     * @return the instance
     */
    public final SqlEngine getSqlEngine() {
        return sqlEngine;
    }

    /**
     * Gets the {@link SqlTable} instance
     *
     * @return the instance
     */
    public final SqlTable getTable() {
        return table;
    }

    /**
     * Gets the column name that is used to detect an entry in the table
     *
     * @return the column name
     */
    public final String getKey() {
        return key;
    }

    /**
     * Creates a new element in the table
     *
     * @param value  the first value to be used
     * @param values the other values to be used
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    public void insertValues(Object value, Object... values) throws SQLException {
        Object[] vals = new Object[values.length + 1];
        vals[0] = value;
        for (int i = 0; i < values.length; i++) {
            vals[i + 1] = values[i];
        }
        insertValues(vals);
    }

    /**
     * Creates a new element in the table
     *
     * @param values the values to be used
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    public void insertValues(Object[] values) throws SQLException {
        SqlTable table = getTable();
        List<SqlTable.Key> defaultKeys = table.getDefaultKeysList();
        if (values.length != defaultKeys.size())
            throw new SQLException("Values do not match the structure of the table!");

        Object[] defaults = table.getDefaultValues();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null)
                continue;
            values[i] = defaults[i];
        }

        if (getSqlEngine().disableAutomaticParameterizedQueries) {
            // INSECURE:
            String cmd = "INSERT INTO `" + table.getName() + "` (";

            for (SqlTable.Key key : defaultKeys) {
                cmd += "`" + key + "`, ";
            }
            if (cmd.endsWith(", ")) cmd = cmd.substring(0, cmd.length() - 2);
            cmd += ") VALUES (";
            for (Object value : values) {
                if (!(value instanceof Number))
                    cmd += "'" + value + "', ";
                else
                    cmd += value + ", ";
            }
            if (cmd.endsWith(", ")) cmd = cmd.substring(0, cmd.length() - 2);
            cmd += ")";

            execute(cmd);
            return;
        }

        Object[] params = new Object[defaultKeys.size() + values.length];
        String cmd = "insert into ? (";

        for (int i = 0; i < defaultKeys.size(); i++) {
            cmd += "?, ";
            params[i] = defaultKeys.get(i);
        }
        if (cmd.endsWith(", ")) cmd = cmd.substring(0, cmd.length() - 2);

        cmd += ") values (";

        for (int i = 0; i < values.length; i++) {
            cmd += "?, ";
            params[defaultKeys.size() + i] = values[i];
        }
        if (cmd.endsWith(", ")) cmd = cmd.substring(0, cmd.length() - 2);
        cmd += ")";

        validateConnection();
        getSqlEngine().execute(cmd, table.getName(), params);


    }

    /**
     * Fetches data from the database
     *
     * @param column the column to fetch the value out of
     * @param keyVal the value to detect the correct entry
     * @return the fetched data
     */
    public ResultSet select(String column, V keyVal) {
        try {
            return query("select %s from %s where %s = '%s'", column, table.name(), key, keyVal);
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    public Object selectObject(String column, V keyVal) {
        try {
            ResultSet rs = select(column, keyVal);
            if (rs.next())
                return rs.getObject(column);
            throw new IllegalArgumentException("Nothing found with given parameters!");
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    public String selectString(String column, V keyVal) {
        try {
            ResultSet rs = select(column, keyVal);
            if (rs.next())
                return rs.getString(column);
            throw new IllegalArgumentException("Nothing found with given parameters!");
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    public boolean selectBoolean(String column, V keyVal) {
        try {
            ResultSet rs = select(column, keyVal);
            if (rs.next())
                return rs.getBoolean(column);
            throw new IllegalArgumentException("Nothing found with given parameters!");
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    public int selectInt(String column, V keyVal) {
        try {
            ResultSet rs = select(column, keyVal);
            if (rs.next())
                return rs.getInt(column);
            throw new IllegalArgumentException("Nothing found with given parameters!");
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    public long selectLong(String column, V keyVal) {
        try {
            ResultSet rs = select(column, keyVal);
            if (rs.next())
                return rs.getLong(column);
            throw new IllegalArgumentException("Nothing found with given parameters!");
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    public double selectDouble(String column, V keyVal) {
        try {
            ResultSet rs = select(column, keyVal);
            if (rs.next())
                return rs.getDouble(column);
            throw new IllegalArgumentException("Nothing found with given parameters!");
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    /**
     * Updates a value to the database
     *
     * @param column the column to update
     * @param value  the value to set
     * @param keyVal the value to detect the correct entry
     */
    public void update(String column, Object value, V keyVal) {
        try {
            execute("update %s set %s = '%s' where %s = '%s'", table.name(), column, value, key, keyVal);
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    /**
     * Deletes an entry from the table
     *
     * @param keyVal the value to detect the correct entry
     */
    public void delete(V keyVal) {
        try {
            execute("delete from %s where %s = '%s'", table.name(), key, keyVal);
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    /**
     * Checks if a certain entry exists in the table
     *
     * @param keyVal the value to detect the correct entry
     * @return true if it exists, otherwise false
     */
    public boolean exists(V keyVal) {
        try {
            return query("select * from %s where %s = '%s'", table.name(), key, keyVal).next();
        } catch (SQLException e) {
            throw new Error(e);
        }
    }

    /**
     * Executes a sql query. Uses {@link String#format(String, Object...)}
     *
     * @param sql    the query
     * @param params the parameters for {@link String#format(String, Object...)}
     * @return the query result
     * @throws SQLException if a database access error occurs,
     *                      this method is called on a closed <code>Statement</code>, the given
     *                      SQL statement produces anything other than a single
     *                      <code>ResultSet</code> object, the method is called on a
     *                      <code>PreparedStatement</code> or <code>CallableStatement</code>
     */
    protected final ResultSet query(String sql, Object... params) throws SQLException {
        validateConnection();
        return sqlEngine.query(String.format(sql, params));
    }

    /**
     * Executes a sql update. Uses {@link String#format(String, Object...)}
     *
     * @param sql    the query
     * @param params the parameters for {@link String#format(String, Object...)}
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     */
    protected final void execute(String sql, Object... params) throws SQLException {
        validateConnection();
        sqlEngine.execute(String.format(sql, params));
    }

    private void validateConnection() {
        try {
            if (!sqlEngine.isConnected())
                sqlEngine.connect();
        } catch (SQLException | ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new Error(e);
        }
    }

}
