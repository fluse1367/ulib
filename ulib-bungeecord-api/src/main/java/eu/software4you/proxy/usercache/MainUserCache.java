package eu.software4you.proxy.usercache;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.proxy.plugin.ExtendedPlugin;
import eu.software4you.reflection.ReflectUtil;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;

import java.io.File;

public class MainUserCache {
    private static UserCache mainCache;
    private static ExtendedPlugin plugin;

    public static void setPlugin(ExtendedPlugin plugin) {
        if (MainUserCache.plugin != null)
            throw new IllegalStateException(String.format("Main user cache already initialized by %s", plugin.getDescription().getName()));
        MainUserCache.plugin = plugin;
        ULib.getInstance().debugImplementation("Main User Cache");
    }

    public static boolean isMainUserCacheEnabled() {
        return mainCache != null;
    }

    @SneakyThrows
    public static void enableMainUserCache() {
        if (isMainUserCacheEnabled())
            return;
        ULib.getInstance().debug("Enabling main user cache! Enabled by " + ReflectUtil.getCallerClassName());

        SqlEngine engine = new SqlEngine();
        engine.disableAutomaticParameterizedQueries = true;

        ConfigurationWrapper backend = plugin.getConfig().sub("user-cache-backend");
        switch (backend.string("type").toUpperCase()) {
            case "FILE":
                engine.setConnectionData(new SqlEngine.ConnectionData(new File(plugin.getDataFolder(), "user_cache.db")));
                break;
            case "MYSQL":
                ConfigurationWrapper login = backend.sub("login");
                engine.setConnectionData(new SqlEngine.ConnectionData(
                        login.string("host"), login.string("user"),
                        login.string("password"), login.string("database")));
                break;
            default:
                throw new IllegalArgumentException(String.format("Backend type must be either FILE or MYSQL, %s is not allowed", backend.string("type")));
        }

        SqlTable table = engine.newTable("cached_users");
        SqlTable.Key primary;
        table.addDefaultKey(primary = new SqlTable.Key("uuid", SqlTable.Key.KeyType.VariableCharacter).size(36));
        table.addDefaultKey(new SqlTable.Key("name", SqlTable.Key.KeyType.VariableCharacter).size(16));
        table.setPrimaryKey(primary);

        engine.addDefaultTable(table);
        engine.connect(true);

        MainUserCache.mainCache = new UserCache(plugin, engine, table.name());
        plugin.getProxy().getPluginManager().registerListener(plugin, MainUserCache.mainCache);
    }

    public static UserCache getMainUserCache() {
        return mainCache;
    }
}
