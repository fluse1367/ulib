package eu.software4you.ulib.impl.minecraft.usercache;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MainUserCache extends eu.software4you.ulib.minecraft.usercache.MainUserCache {
    private final PluginBase<?, ?, ?> plugin;
    private final SqlEngine engine;

    @SneakyThrows
    @Override
    protected UserCache enable0() {
        engine.disableAutomaticParameterizedQueries = true;
        var backend = plugin.getConf().getSub("user-cache-backend");
        switch (backend.string("type", "FILE").toUpperCase()) {
            case "FILE" -> engine.setConnectionData(new SqlEngine.ConnectionData(new File(plugin.getDataFolder(), "user_cache.db")));
            case "MYSQL" -> {
                var login = backend.getSub("login");
                engine.setConnectionData(new SqlEngine.ConnectionData(
                        login.string("host", "localhost"), login.string("user", "root"),
                        login.string("password", "root"), login.string("database", "mainusercache")));
            }
            default -> throw new IllegalArgumentException(String.format("Backend type must be either FILE or MYSQL, %s is not allowed", backend.string("type", "null")));
        }

        SqlTable table = engine.newTable("cached_users");
        SqlTable.Key primary;
        table.addDefaultKey(primary = new SqlTable.Key("uuid", SqlTable.Key.KeyType.VariableCharacter).size(36));
        table.addDefaultKey(new SqlTable.Key("name", SqlTable.Key.KeyType.VariableCharacter).size(16));
        table.setPrimaryKey(primary);

        engine.addDefaultTable(table);
        engine.connect(true);

        return UserCache.of(plugin, engine, table.name());
    }
}
