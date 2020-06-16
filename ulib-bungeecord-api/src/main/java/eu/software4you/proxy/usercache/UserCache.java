package eu.software4you.proxy.usercache;

import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.sql.SqlTableWrapper;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserCache implements Listener {
    private final Plugin owner;
    private final SqlEngine sqlEngine;
    private final SqlTable table;
    private final HashMap<UUID, String> cache = new HashMap<>();

    public UserCache(Plugin owner, SqlEngine sqlEngine, String tableName) {
        this(owner, sqlEngine, sqlEngine.getDefaultTables().get(tableName));
    }

    public UserCache(Plugin owner, SqlEngine sqlEngine, SqlTable table) {
        this.owner = owner;
        this.sqlEngine = sqlEngine;
        this.table = table;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PostLoginEvent e) {
        owner.getProxy().getScheduler().runAsync(owner, () -> cache(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerDisconnectEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    public boolean cache(ProxiedPlayer proxiedPlayer) {
        return cache(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());
    }

    public boolean cache(UUID uuid, String name) {
        try {
            if (getUsername(uuid) == null) {
                new SqlTableWrapper<String>(sqlEngine, table, "uuid").insertValues(uuid.toString(), name);
            } else {
                sqlEngine.execute(String.format("update %s set name = '%s' where uuid = '%s'", table.name(), name, uuid.toString()));
            }
            cache.put(uuid, name);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean purge(UUID uuid) {
        if (getUsername(uuid) == null)
            return true;
        try {
            sqlEngine.execute(String.format("delete from from %s where uuid = '%s'", table.name(), uuid.toString()));
            cache.remove(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean purge(String username) {
        UUID uuid;
        if ((uuid = getUUID(username)) == null)
            return true;
        return purge(uuid);
    }

    public String getUsername(UUID uuid) {
        if (!cache.containsKey(uuid))
            try {
                ResultSet rs = sqlEngine.query(
                        String.format("select name from %s where uuid = '%s'", table.name(), uuid.toString()));
                if (rs.next()) {
                    cache.put(uuid, rs.getString("name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        return cache.get(uuid);
    }

    public UUID getUUID(String username) {
        if (!cache.containsValue(username)) {
            try {
                ResultSet rs = sqlEngine.query(
                        String.format("select uuid from %s where name = '%s'", table.name(), username));
                if (rs.next()) {
                    cache.put(UUID.fromString(rs.getString("uuid")), username);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        for (Map.Entry<UUID, String> entry : cache.entrySet()) {
            if (entry.getValue().equals(username))
                return entry.getKey();
        }
        return null;
    }

}
