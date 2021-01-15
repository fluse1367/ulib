package eu.software4you.ulib;

import eu.software4you.bungeecord.plugin.ExtendedPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

class UserCacheImpl extends UserCache implements Listener {
    private final ExtendedPlugin owner;

    protected UserCacheImpl(PluginBase<?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        super(owner, sqlEngine, table);
        if (!(owner instanceof ExtendedPlugin))
            throw new IllegalStateException("Implementation of wrong type.");
        this.owner = (ExtendedPlugin) owner;
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
