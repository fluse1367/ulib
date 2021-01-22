package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.function.TriFunction;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.minecraft.plugin.PluginBase;

public class UserCacheInit {
    public static void constructor(TriFunction<PluginBase<?, ?, ?>, SqlEngine, SqlTable, UserCache> constructor) {
        UserCache.constructor = constructor;
    }

    public static void pluginBase(PluginBase<?, ?, ?> plugin) {
        MainUserCache.plugin = plugin;
    }

    public static void engine(SqlEngine engine) {
        MainUserCache.engine = engine;
    }
}
