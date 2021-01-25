package eu.software4you.ulib.impl.bungeecord.usercache;

import eu.software4you.bungeecord.plugin.ExtendedPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.ImplRegistry;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class UserCacheImpl extends UserCache implements Listener {

    static {
        ImplRegistry.put(UserCache.class, UserCacheImpl::new);
    }

    private final ExtendedPlugin owner;

    private UserCacheImpl(Object[] params) {
        super((SqlEngine) params[1], (SqlTable) params[2]);
        this.owner = (ExtendedPlugin) params[0];
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
