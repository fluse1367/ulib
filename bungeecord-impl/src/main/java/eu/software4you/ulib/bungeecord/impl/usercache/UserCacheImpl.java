package eu.software4you.ulib.bungeecord.impl.usercache;

import eu.software4you.ulib.bungeecord.api.plugin.ExtendedPlugin;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.minecraft.api.internal.Providers;
import eu.software4you.ulib.minecraft.api.plugin.PluginBase;
import eu.software4you.ulib.minecraft.api.usercache.UserCache;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class UserCacheImpl extends eu.software4you.ulib.minecraft.impl.usercache.UserCache implements Listener {
    private final ExtendedPlugin owner;

    private UserCacheImpl(ExtendedPlugin owner, SqlEngine engine, SqlTable table) {
        super(engine, table);
        this.owner = owner;
        this.owner.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PostLoginEvent e) {
        owner.getProxy().getScheduler().runAsync(owner, () -> cache(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerDisconnectEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    public void cache(ProxiedPlayer proxiedPlayer) {
        cache(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());
    }

    public static final class UserCacheProvider implements Providers.ProviderUserCache {
        @Override
        public UserCache provide(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
            return new UserCacheImpl((ExtendedPlugin) owner, sqlEngine, table);
        }
    }

}
