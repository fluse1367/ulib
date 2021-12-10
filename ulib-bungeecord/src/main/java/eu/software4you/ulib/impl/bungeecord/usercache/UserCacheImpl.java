package eu.software4you.ulib.impl.bungeecord.usercache;

import eu.software4you.bungeecord.plugin.ExtendedPlugin;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.inject.Factory;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

@Impl(UserCache.class)
public final class UserCacheImpl extends eu.software4you.ulib.impl.minecraft.usercache.UserCache implements Listener {
    private final ExtendedPlugin owner;

    @Factory
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
}
