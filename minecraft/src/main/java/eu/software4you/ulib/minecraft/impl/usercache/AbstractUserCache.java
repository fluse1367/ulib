package eu.software4you.ulib.minecraft.impl.usercache;

import eu.software4you.ulib.core.database.Database;
import eu.software4you.ulib.core.database.sql.*;
import eu.software4you.ulib.core.util.LazyValue;
import eu.software4you.ulib.core.util.SingletonInstance;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

public abstract class AbstractUserCache implements UserCache {

    public static final SingletonInstance<BiFunction<PluginBase<?, ?>, Table, AbstractUserCache>> PROVIDER = new SingletonInstance<>();
    // TODO: supply the singleton
    public static final SingletonInstance<PluginBase<?, ?>> PLUGIN_INSTANCE = new SingletonInstance<>();
    public static final LazyValue<UserCache> MAIN_CACHE = new LazyValue<>(AbstractUserCache::getMainCache);

    private static UserCache getMainCache() {
        var plugin = PLUGIN_INSTANCE.get();

        // init database connection
        SqlDatabase database;
        var backend = plugin.getConf().getSub("user-cache-backend").orElseThrow();
        switch (backend.string("type").orElse("FILE").toUpperCase()) {
            case "FILE" -> database = Database.prepare(new File(plugin.getDataFolder(), "user_cache.db"));
            case "MYSQL" -> {
                var login = backend.getSub("login").orElseThrow();
                database = Database.prepare(
                        login.string("host").orElse("localhost"),
                        login.int32("port").orElse(3306),
                        login.string("database").orElse("mainusercache"),
                        login.string("user").orElse("root"),
                        login.string("password").orElse("root")
                );
            }
            default -> throw new IllegalArgumentException(String.format("Backend type must be either FILE or MYSQL, %s is not allowed", backend.string("type", "null")));
        }

        var table = database.addTable("cached_users",
                ColumnBuilder.of("uuid", DataType.VARCHAR).size(36).primary(),
                ColumnBuilder.of("name", DataType.VARCHAR).size(16).notNull()
        );


        database.connect();

        if (!table.exists())
            table.create();

        return PROVIDER.get().apply(plugin, table);
    }

    private final Table table;
    private final Map<UUID, String> cache = new ConcurrentHashMap<>();

    protected AbstractUserCache(Table table) {
        this.table = table;
    }

    @SneakyThrows
    public void cache(UUID uuid, String name) {
        if (getUsername(uuid) == null) {
            table.insert(uuid.toString(), name);
        } else {
            table.update().setP("name", name).where("uuid").isEqualToP(uuid.toString());
        }
        cache.put(uuid, name);
    }

    @SneakyThrows
    public void purge(UUID uuid) {
        if (getUsername(uuid) == null)
            return;
        table.delete().where("uuid").isEqualToP(uuid.toString()).update();
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
            var rs = table.select("name").where("uuid").isEqualToP(uuid.toString()).query();
            if (rs.next()) {
                cache.put(uuid, rs.getString("name"));
            }
        }
        return cache.get(uuid);
    }

    @SneakyThrows
    public UUID getUUID(String username) {
        if (!cache.containsValue(username)) {
            var rs = table.select("uuid").where("name").isEqualToP(username).query();
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
