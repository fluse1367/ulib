package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import lombok.SneakyThrows;

import java.io.File;

public class MainUserCache {
    static PluginBase<?, ?, ?> plugin;
    static SqlEngine engine;
    static UserCache mainCache;

    public static boolean isEnabled() {
        return mainCache != null;
    }

    @SneakyThrows
    public static void enable() {
        if (isEnabled())
            return;
        ULib.getInstance().debug("Enabling main user cache! Enabled by " + ReflectUtil.getCallerClassName());

        engine.disableAutomaticParameterizedQueries = true;

        ConfigurationWrapper backend = plugin.getConf().sub("user-cache-backend");
        switch (backend.string("type", "FILE").toUpperCase()) {
            case "FILE":
                engine.setConnectionData(new SqlEngine.ConnectionData(new File(plugin.getDataFolder(), "user_cache.db")));
                break;
            case "MYSQL":
                ConfigurationWrapper login = backend.sub("login");
                engine.setConnectionData(new SqlEngine.ConnectionData(
                        login.string("host", "localhost"), login.string("user", "root"),
                        login.string("password", "root"), login.string("database", "mainusercache")));
                break;
            default:
                throw new IllegalArgumentException(String.format("Backend type must be either FILE or MYSQL, %s is not allowed", backend.string("type", "null")));
        }

        SqlTable table = engine.newTable("cached_users");
        SqlTable.Key primary;
        table.addDefaultKey(primary = new SqlTable.Key("uuid", SqlTable.Key.KeyType.VariableCharacter).size(36));
        table.addDefaultKey(new SqlTable.Key("name", SqlTable.Key.KeyType.VariableCharacter).size(16));
        table.setPrimaryKey(primary);

        engine.addDefaultTable(table);
        engine.connect(true);

        MainUserCache.mainCache = UserCache.of(plugin, engine, table.name());
    }

    public static UserCache get() {
        return mainCache;
    }
}