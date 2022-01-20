package eu.software4you.ulib.bungeecord.impl.usercache;

import eu.software4you.ulib.bungeecord.impl.PluginSubst;
import eu.software4you.ulib.core.api.sql.SqlEngine;


public final class MainUserCacheImpl extends eu.software4you.ulib.minecraft.impl.usercache.MainUserCache {
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
