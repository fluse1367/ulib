package eu.software4you.ulib.spigot.impl.usercache;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.minecraft.impl.usercache.MainUserCache;
import eu.software4you.ulib.spigot.impl.PluginSubst;

public final class MainUserCacheImpl extends MainUserCache {

    private static PluginSubst plugin;
    private static SqlEngine engine;

    public MainUserCacheImpl() {
        super(plugin, engine);
    }

    public static void init(PluginSubst pl, SqlEngine engine) {
        MainUserCacheImpl.plugin = pl;
        MainUserCacheImpl.engine = engine;
    }
}
