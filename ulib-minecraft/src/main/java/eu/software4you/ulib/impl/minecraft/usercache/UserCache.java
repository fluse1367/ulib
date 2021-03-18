package eu.software4you.ulib.impl.minecraft.usercache;

import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.sql.SqlTableWrapper;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

public abstract class UserCache extends eu.software4you.ulib.minecraft.usercache.UserCache {
    protected UserCache(SqlEngine sqlEngine, SqlTable table) {
        super(sqlEngine, table);
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
