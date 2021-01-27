package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MainUserCache {
    @Await
    private static MainUserCache impl;
    private static UserCache mainCache;
    private final PluginBase<?, ?, ?> plugin;
    private final SqlEngine engine;

    public static boolean isEnabled() {
        return mainCache != null;
    }

    @SneakyThrows
    public static void enable() {
        if (isEnabled())
            return;
        ULib.get().debug("Enabling main user cache! Enabled by " + ReflectUtil.getCallerClassName());

        impl.engine.disableAutomaticParameterizedQueries = true;

        ConfigurationWrapper backend = impl.plugin.getConf().sub("user-cache-backend");
        switch (backend.string("type", "FILE").toUpperCase()) {
            case "FILE":
                impl.engine.setConnectionData(new SqlEngine.ConnectionData(new File(impl.plugin.getDataFolder(), "user_cache.db")));
                break;
            case "MYSQL":
                ConfigurationWrapper login = backend.sub("login");
                impl.engine.setConnectionData(new SqlEngine.ConnectionData(
                        login.string("host", "localhost"), login.string("user", "root"),
                        login.string("password", "root"), login.string("database", "mainusercache")));
                break;
            default:
                throw new IllegalArgumentException(String.format("Backend type must be either FILE or MYSQL, %s is not allowed", backend.string("type", "null")));
        }

        SqlTable table = impl.engine.newTable("cached_users");
        SqlTable.Key primary;
        table.addDefaultKey(primary = new SqlTable.Key("uuid", SqlTable.Key.KeyType.VariableCharacter).size(36));
        table.addDefaultKey(new SqlTable.Key("name", SqlTable.Key.KeyType.VariableCharacter).size(16));
        table.setPrimaryKey(primary);

        impl.engine.addDefaultTable(table);
        impl.engine.connect(true);

        MainUserCache.mainCache = UserCache.of(impl.plugin, impl.engine, table.name());
    }

    public static UserCache get() {
        return mainCache;
    }
}
