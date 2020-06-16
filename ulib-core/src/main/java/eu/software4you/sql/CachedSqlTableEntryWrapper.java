package eu.software4you.sql;

import java.util.Collections;
import java.util.Map;

/**
 * Same as {@link SqlTableEntryWrapper}, but with caching.
 */
public class CachedSqlTableEntryWrapper<V> extends SqlTableEntryWrapper<V> {
    private final CachedSqlTableWrapper<V> wrapper;

    /**
     * @see SqlTableEntryWrapper#SqlTableEntryWrapper(SqlEngine, SqlTable, String, Object)
     */
    public CachedSqlTableEntryWrapper(SqlEngine engine, SqlTable table, String key, V keyVal) {
        super(new CachedSqlTableWrapper<>(engine, table, key), keyVal);
        this.wrapper = (CachedSqlTableWrapper<V>) super.wrapper;
    }

    /**
     * Caches the entry of the table
     */
    public final void cache() {
        wrapper.cache(getKeyVal());
    }

    /**
     * Deletes the whole cache
     */
    public final void purgeCache() {
        wrapper.purgeCache();
    }

    /**
     * Gets an immutable copy of the whole (current) cache
     *
     * @return an immutable copy of the whole cache
     */
    public final Map<String, Object> getCache() {
        return Collections.unmodifiableMap(wrapper.getCache().get(getKeyVal()));
    }
}
