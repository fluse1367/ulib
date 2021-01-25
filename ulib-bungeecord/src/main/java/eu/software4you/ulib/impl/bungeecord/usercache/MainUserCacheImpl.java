package eu.software4you.ulib.impl.bungeecord.usercache;

import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.ImplRegistry;
import eu.software4you.ulib.ULibBungeecordPlugin;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.MainUserCache;

public final class MainUserCacheImpl extends MainUserCache {
    private MainUserCacheImpl(PluginBase<?, ?, ?> plugin, SqlEngine engine) {
        super(plugin, engine);
    }

    public static void init(ULibBungeecordPlugin pl, SqlEngine engine) {
        ImplRegistry.put(MainUserCache.class, new MainUserCacheImpl(pl, engine));
    }
}