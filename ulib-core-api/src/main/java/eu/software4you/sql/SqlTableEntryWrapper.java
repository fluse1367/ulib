package eu.software4you.sql;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Same as {@link SqlTableWrapper}, but with a static value to detect the correct entry in the table.
 * So this wrapper is for a <b>single</b> table entry.
 */
public class SqlTableEntryWrapper<V> {
    protected final SqlTableWrapper<V> wrapper;
    private final V keyVal;

    SqlTableEntryWrapper(SqlTableWrapper<V> wrapper, V keyVal) {
        this.wrapper = wrapper;
        this.keyVal = keyVal;
    }

    /**
     * @param keyVal the value that is used to detect the correct entry in the table
     * @see SqlTableWrapper#SqlTableWrapper(SqlEngine, SqlTable, String)
     */
    public SqlTableEntryWrapper(SqlEngine engine, SqlTable table, String key, V keyVal) {
        this.wrapper = new SqlTableWrapper<>(engine, table, key);
        this.keyVal = keyVal;
    }

    /**
     * Gets the {@link SqlEngine} instance
     *
     * @return the instance
     */
    public final SqlEngine getSqlEngine() {
        return wrapper.getSqlEngine();
    }

    /**
     * Gets the {@link SqlTable} instance
     *
     * @return the instance
     */
    public final SqlTable getTable() {
        return wrapper.getTable();
    }

    /**
     * Gets the column name that is used to detect an entry in the table
     *
     * @return the column name
     */
    public final String getKey() {
        return wrapper.getKey();
    }


    /**
     * Gets the value that is used to detect an entry win the table
     *
     * @return the value
     */
    public final V getKeyVal() {
        return keyVal;
    }

    /**
     * Fetches data from the database
     *
     * @param column the column to fetch the value out of
     * @return the fetched data
     */
    public ResultSet select(String column) {
        return wrapper.select(column, keyVal);
    }

    public Object selectObject(String column) {
        return wrapper.selectObject(column, keyVal);
    }

    public String selectString(String column) {
        return wrapper.selectString(column, keyVal);
    }

    public boolean selectBoolean(String column) {
        return wrapper.selectBoolean(column, keyVal);
    }

    public int selectInt(String column) {
        return wrapper.selectInt(column, keyVal);
    }

    public long selectLong(String column) {
        return wrapper.selectLong(column, keyVal);
    }

    public double selectDouble(String column) {
        return wrapper.selectDouble(column, keyVal);
    }

    /**
     * Updates a value to the database
     *
     * @param column the column to update
     * @param value  the value to set
     */
    public void update(String column, Object value) {
        wrapper.update(column, value, keyVal);
    }

    /**
     * Deletes the entry from the table
     */
    public void delete() {
        wrapper.delete(keyVal);
    }

    /**
     * Checks if the entry exists in the table
     *
     * @return true if it exists, otherwise false
     */
    public boolean exists() {
        return wrapper.exists(keyVal);
    }

    /**
     * Creates a new element in the table.
     * <b>This method does not automatically uses the {@code keyVal}!</b>
     *
     * @param value  the first value to be used
     * @param values the other values to be used
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     * @see SqlTableWrapper#insertValues(Object, Object...)
     */
    public void insertValues(Object value, Object... values) throws SQLException {
        wrapper.insertValues(value, values);
    }

    /**
     * Creates a new element in the table.
     * <b>This method does not automatically uses the {@code keyVal}!</b>
     *
     * @param values the values to be used
     * @throws SQLException if a database access error occurs;
     *                      this method is called on a closed  <code>PreparedStatement</code>
     *                      or the SQL statement returns a <code>ResultSet</code> object
     * @see SqlTableWrapper#insertValues(Object[])
     */
    public void insertValues(Object[] values) throws SQLException {
        wrapper.insertValues(values);
    }


}
