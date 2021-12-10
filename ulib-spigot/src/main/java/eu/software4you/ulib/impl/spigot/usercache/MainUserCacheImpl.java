package eu.software4you.ulib.impl.spigot.usercache;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.ImplInjector;
import eu.software4you.ulib.ULibSpigotPlugin;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.MainUserCache;

public final class MainUserCacheImpl extends eu.software4you.ulib.impl.minecraft.usercache.MainUserCache {

    private MainUserCacheImpl(PluginBase<?, ?, ?> plugin, SqlEngine engine) {
        super(plugin, engine);
    }

    public static void init(ULibSpigotPlugin pl, SqlEngine engine) {
        ImplInjector.inject(new MainUserCacheImpl(pl, engine), MainUserCache.class);
    }
}
