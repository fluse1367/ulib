package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.function.ConstructingFunction;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.UUID;

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

    public static UserCache of(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, String tableName) {
        return of(owner, sqlEngine, sqlEngine.getDefaultTables().get(tableName));
    }

    @SneakyThrows
    public static UserCache of(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        return constructor.apply(owner, sqlEngine, table);
    }

    public abstract void cache(UUID uuid, String name);

    public abstract void purge(UUID uuid);

    public abstract void purge(String username);

    public abstract String getUsername(UUID uuid);

    public abstract UUID getUUID(String username);

}
