package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.ulib.core.database.sql.SqlDatabase;
import eu.software4you.ulib.core.database.sql.Table;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * A cache for UUIDs and their associated usernames. Realized with a sql database.
 */
public interface UserCache {
    /**
     * Creates a new user cache.
     *
     * @param owner     the owning plugin.
     * @param database  the sql database that should be used
     * @param tableName the table name for the data
     * @return the newly created user cache instance
     */
    @NotNull
    static UserCache of(@NotNull PluginBase<?, ?> owner, @NotNull SqlDatabase database, @NotNull String tableName) {
        return of(owner, database.getTable(tableName).orElseThrow());
    }

    /**
     * Creates a new user cache. The table must have the columns {@code uuid} (varchar(36)) and {@code name} (varchar(36)).
     *
     * @param owner the owning plugin.
     * @param table the (sql) table for the data
     * @return the newly created user cache instance
     */
    @NotNull
    static UserCache of(@NotNull PluginBase<?, ?> owner, @NotNull Table table) {
        return AbstractUserCache.PROVIDER.get().apply(owner, table);
    }

    /**
     * Enables the main user cache managed by ulib itself if it is not already enabled.
     *
     * @return the main user cache
     */
    @NotNull
    static UserCache getMainCache() {
        //noinspection ConstantConditions
        return AbstractUserCache.MAIN_CACHE.get();
    }

    /**
     * Checks weather the main user cache is enabled.
     *
     * @return {@code true} if the main cache is enabled, {@code false} otherwise
     */
    static boolean isMainCache() {
        return AbstractUserCache.MAIN_CACHE_DB.isSet();
    }

    /**
     * Caches (or updates) a UUID and its associated username.
     *
     * @param uuid the uuid
     * @param name the username
     */
    void cache(@NotNull UUID uuid, @NotNull String name);

    /**
     * Removes a UUID (and its associated username) from the cache.
     *
     * @param uuid the UUID to remove
     */
    void purge(@NotNull UUID uuid);

    /**
     * Removes a username (and its associated UUID) from the cache.
     *
     * @param username the username to remove
     */
    void purge(@NotNull String username);

    /**
     * Fetches a username from the cache.
     *
     * @param uuid the username's associated UUID
     * @return the username, or {@code null} if the UUID is not cached
     */
    @NotNull
    Optional<String> getUsername(@NotNull UUID uuid);

    /**
     * Fetches a UUID from the cache.
     *
     * @param username the UUID's associated username
     * @return the UUID, or {@code null} if the username is not cached
     */
    @NotNull
    Optional<UUID> getUUID(@NotNull String username);

}
