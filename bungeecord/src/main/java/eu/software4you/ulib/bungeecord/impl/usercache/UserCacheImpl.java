package eu.software4you.ulib.bungeecord.impl.usercache;

import eu.software4you.ulib.core.database.sql.Table;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public final class UserCacheImpl extends AbstractUserCache implements Listener {
    private final PluginBase<Listener, ?> owner;

    public UserCacheImpl(PluginBase<?, ?> pl, Table table) {
        super(table);
        //noinspection unchecked
        this.owner = (PluginBase<Listener, ?>) pl;
        this.owner.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PostLoginEvent e) {
        owner.async(() -> cache(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerDisconnectEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    public void cache(ProxiedPlayer proxiedPlayer) {
        cache(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());
    }

}
