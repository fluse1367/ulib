package eu.software4you.ulib.spigot.impl.usercache;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.minecraft.api.internal.Providers;
import eu.software4you.ulib.minecraft.api.plugin.PluginBase;
import eu.software4you.ulib.minecraft.api.usercache.UserCache;
import eu.software4you.ulib.spigot.api.plugin.ExtendedPlugin;

public final class UserCacheProvider implements Providers.ProviderUserCache {
    @Override
    public UserCache provide(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        return new UserCacheImpl((ExtendedPlugin) owner, sqlEngine, table);
    }
}
