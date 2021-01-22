package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.sql.SqlTableWrapper;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import lombok.SneakyThrows;
import lombok.val;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class UserCache {
    static Class<? extends UserCache> implClazz;
    protected final HashMap<UUID, String> cache = new HashMap<>();
    protected final SqlEngine sqlEngine;
    protected final SqlTable table;

    protected UserCache(SqlEngine sqlEngine, SqlTable table) {
        this.sqlEngine = sqlEngine;
        this.table = table;
    }

    public static UserCache of(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, String tableName) {
        return of(owner, sqlEngine, sqlEngine.getDefaultTables().get(tableName));
    }

    @SneakyThrows
    public static UserCache of(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        if (UserCache.implClazz == null)
            throw new IllegalStateException("User Cache not initialized");
        val constructor = implClazz.getConstructor(PluginBase.class, SqlEngine.class, SqlTable.class);
        if (!constructor.isAccessible())
            throw new RuntimeException("Invalid User Cache implementation");

        return constructor.newInstance(owner, sqlEngine, table);
    }

    @SneakyThrows
    public void cache(UUID uuid, String name) {
        if (getUsername(uuid) == null) {
            new SqlTableWrapper<String>(sqlEngine, table, "uuid").insertValues(uuid.toString(), name);
        } else {
            sqlEngine.execute(String.format("update %s set name = '%s' where uuid = '%s'", table.name(), name, uuid.toString()));
        }
        cache.put(uuid, name);
    }

    @SneakyThrows
    public void purge(UUID uuid) {
        if (getUsername(uuid) == null)
            return;
        sqlEngine.execute(String.format("delete from from %s where uuid = '%s'", table.name(), uuid.toString()));
        cache.remove(uuid);
    }

    public void purge(String username) {
        UUID uuid;
        if ((uuid = getUUID(username)) == null)
            return;
        purge(uuid);
    }

    @SneakyThrows
    public String getUsername(UUID uuid) {
        if (!cache.containsKey(uuid)) {
            ResultSet rs = sqlEngine.query(
                    String.format("select name from %s where uuid = '%s'", table.name(), uuid.toString()));
            if (rs.next()) {
                cache.put(uuid, rs.getString("name"));
            }
        }
        return cache.get(uuid);
    }

    @SneakyThrows
    public UUID getUUID(String username) {
        if (!cache.containsValue(username)) {
            ResultSet rs = sqlEngine.query(
                    String.format("select uuid from %s where name = '%s'", table.name(), username));
            if (rs.next()) {
                cache.put(UUID.fromString(rs.getString("uuid")), username);
            }
        }
        for (Map.Entry<UUID, String> entry : cache.entrySet()) {
            if (entry.getValue().equals(username))
                return entry.getKey();
        }
        return null;
    }

}
