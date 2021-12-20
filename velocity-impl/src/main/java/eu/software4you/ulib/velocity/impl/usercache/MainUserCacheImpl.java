package eu.software4you.ulib.velocity.impl.usercache;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.minecraft.impl.usercache.MainUserCache;
import eu.software4you.ulib.velocity.api.plugin.VelocityJavaPlugin;

public final class MainUserCacheImpl extends MainUserCache {

    private static VelocityJavaPlugin plugin;
    private static SqlEngine engine;

    public MainUserCacheImpl() {
        super(plugin, engine);
    }

    public static void init(VelocityJavaPlugin plugin, SqlEngine engine) {
        MainUserCacheImpl.plugin = plugin;
        MainUserCacheImpl.engine = engine;
    }
}
