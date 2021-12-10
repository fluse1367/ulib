package eu.software4you.ulib.core.api.sql;

import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.*;

/**
 * Same as {@link SqlTableWrapper}, but with caching.
 */
public class CachedSqlTableWrapper<V> extends SqlTableWrapper<V> {
    private final Map<V, Map<String, Object>> values = new HashMap<>();
    private final List<String> columnNames;

    /**
     * @see SqlTableWrapper#SqlTableWrapper(SqlEngine, SqlTable, String)
     */
    @SneakyThrows
    public CachedSqlTableWrapper(SqlEngine engine, SqlTable table, String key) {
        super(engine, table, key);

        List<String> columnNames = new ArrayList<>();
        ResultSet rs = query("select * from %s", table.name());
        ResultSetMetaData meta = rs.getMetaData();
        for (int i = 1; i <= meta.getColumnCount(); i++) {
            columnNames.add(meta.getColumnName(i));
        }
        this.columnNames = Collections.unmodifiableList(columnNames);
    }


    @Override
    public Object selectObject(String column, V keyVal) {
        Map<String, Object> values = getColumns(keyVal);
        if (!values.containsKey(column)) {
            Object value = super.selectObject(column, keyVal);
            values.put(column, value);
        }
        return values.get(column);
    }

    @Override
    public String selectString(String column, V keyVal) {
        Map<String, Object> values = getColumns(keyVal);
        if (!values.containsKey(column)) {
            String value = super.selectString(column, keyVal);
            values.put(column, value);
        }
        return (String) values.get(column);
    }

    @Override
    public boolean selectBoolean(String column, V keyVal) {
        Map<String, Object> values = getColumns(keyVal);
        if (!values.containsKey(column)) {
            boolean value = super.selectBoolean(column, keyVal);
            values.put(column, value);
        }
        return (boolean) values.get(column);
    }

    @Override
    public int selectInt(String column, V keyVal) {
        Map<String, Object> values = getColumns(keyVal);
        if (!values.containsKey(column)) {
            int value = super.selectInt(column, keyVal);
            values.put(column, value);
        }
        return (int) values.get(column);
    }

    @Override
    public long selectLong(String column, V keyVal) {
        Map<String, Object> values = getColumns(keyVal);
        if (!values.containsKey(column)) {
            long value = super.selectLong(column, keyVal);
            values.put(column, value);
        }
        return (long) values.get(column);
    }

    @Override
    public double selectDouble(String column, V keyVal) {
        Map<String, Object> values = getColumns(keyVal);
        if (!values.containsKey(column)) {
            double value = super.selectDouble(column, keyVal);
            values.put(column, value);
        }
        return (double) values.get(column);
    }

    /**
     * @see SqlTableWrapper#update(String, Object, Object)
     */
    @Override
    public void update(String column, Object value, V keyVal) {
        getColumns(keyVal).put(column, value);
        super.update(column, value, keyVal);
    }

    /**
     * @see SqlTableWrapper#delete(Object)
     */
    @Override
    public void delete(V keyVal) {
        super.delete(keyVal);
        purgeCache(keyVal);
    }

    /**
     * @see SqlTableWrapper#exists(Object)
     */
    @Override
    public boolean exists(V keyVal) {
        if (!values.containsKey(keyVal) && super.exists(keyVal))
            values.put(keyVal, new HashMap<>());
        return values.containsKey(keyVal);
    }

    /**
     * Caches the whole sql table
     */
    @SneakyThrows
    public final void cache() {
        ResultSet rs = query("select * from %s", getTable().name());

        while (rs.next()) {
            V keyVal = (V) rs.getObject(getKey());

            Map<String, Object> values = getColumns(keyVal);

            for (String column : columnNames) {
                values.put(column, rs.getObject(column));
            }

        }
    }

    /**
     * Caches only one specific entry of the sql table
     *
     * @param keyVal the value to detect the entry
     */
    @SneakyThrows
    public final void cache(V keyVal) {
        ResultSet rs = query("select * from %s where %s = '%s'", getTable().name(), getKey(), keyVal);
        if (!rs.next())
            throw new IllegalArgumentException("Nothing found with given parameters!");

        Map<String, Object> values = getColumns(keyVal);

        for (String column : columnNames) {
            values.put(column, rs.getObject(column));
        }
    }

    /**
     * Deletes the whole cache
     */
    public final void purgeCache() {
        values.clear();
    }

    /**
     * Deletes only one specific entry in the cache
     *
     * @param keyVal the value to detect the entry
     */
    public final void purgeCache(V keyVal) {
        if (values.containsKey(keyVal))
            values.get(keyVal).clear();
    }

    /**
     * Gets an immutable copy of the whole (current) cache
     *
     * @return an immutable copy of the whole cache
     */
    public final Map<V, Map<String, Object>> getCache() {
        return Collections.unmodifiableMap(values);
    }

    private Map<String, Object> getColumns(V keyVal) {
        if (!values.containsKey(keyVal))
            values.put(keyVal, new HashMap<>());
        return values.get(keyVal);
    }
}
