package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.minecraft.plugin.PluginBase;

public class UserCacheInit {
    public static void userCache(Class<? extends UserCache> implClazz) {
        UserCache.implClazz = implClazz;
    }

    public static void pluginBase(PluginBase<?, ?, ?> plugin) {
        MainUserCache.plugin = plugin;
    }

    public static void engine(SqlEngine engine) {
        MainUserCache.engine = engine;
    }
}
