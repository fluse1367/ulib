package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.function.ConstructingFunction;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.UUID;

/**
 * A cache for UUIDs and their associated usernames. Realized with a sql database.
 */
public abstract class UserCache {
    @Await
    private static ConstructingFunction<UserCache> constructor;
    protected final HashMap<UUID, String> cache = new HashMap<>();
    protected final SqlEngine sqlEngine;
    protected final SqlTable table;

    protected UserCache(SqlEngine sqlEngine, SqlTable table) {
        this.sqlEngine = sqlEngine;
        this.table = table;
    }

    /**
     * Creates a new user cache.
     *
     * @param owner     the owning plugin.
     * @param sqlEngine the sql engine that should be used
     * @param tableName the table name for the data
     * @return the newly created user cache instance
     */
    public static UserCache of(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, String tableName) {
        return of(owner, sqlEngine, sqlEngine.getDefaultTables().get(tableName));
    }

    /**
     * Creates a new user cache.
     *
     * @param owner     the owning plugin.
     * @param sqlEngine the sql engine that should be used
     * @param table     the (sql) table for the data
     * @return the newly created user cache instance
     */
    @SneakyThrows
    public static UserCache of(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        return constructor.apply(owner, sqlEngine, table);
    }

    /**
     * Caches (or updates) a UUID and its associated username.
     *
     * @param uuid the uuid
     * @param name the username
     */
    public abstract void cache(UUID uuid, String name);

    /**
     * Removes a UUID (and its associated username) from the cache.
     *
     * @param uuid the UUID to remove
     */
    public abstract void purge(UUID uuid);

    /**
     * Removes a username (and its associated UUID) from the cache.
     *
     * @param username the username to remove
     */
    public abstract void purge(String username);

    /**
     * Fetches a username from the cache.
     *
     * @param uuid the username's associated UUID
     * @return the username, or {@code null} if the UUID is not cached
     */
    public abstract String getUsername(UUID uuid);

    /**
     * Fetches a UUID from the cache.
     *
     * @param username the UUID's associated username
     * @return the UUID, or {@code null} if the username is not cached
     */
    public abstract UUID getUUID(String username);

}
