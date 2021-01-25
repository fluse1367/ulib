package eu.software4you.ulib.impl.velocity.usercache;

import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.ImplRegistry;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.MainUserCache;
import eu.software4you.velocity.plugin.VelocityJavaPlugin;

public final class MainUserCacheImpl extends MainUserCache {

    private MainUserCacheImpl(PluginBase<?, ?, ?> plugin, SqlEngine engine) {
        super(plugin, engine);
    }

    public static void init(VelocityJavaPlugin pl, SqlEngine engine) {
        ImplRegistry.put(MainUserCache.class, new MainUserCacheImpl(pl, engine));
    }
}
