package eu.software4you.ulib.minecraft.api.internal;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.minecraft.api.plugin.PluginBase;
import eu.software4you.ulib.minecraft.api.usercache.UserCache;

public final class Providers {
    public interface ProviderUserCache {
        UserCache provide(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table);
    }
}
