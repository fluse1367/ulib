package eu.software4you.ulib.impl.spigot.usercache;

import eu.software4you.spigot.plugin.ExtendedPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.Impl;
import eu.software4you.ulib.ImplConst;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Impl(UserCache.class)
public final class UserCacheImpl extends UserCache implements Listener {
    private final ExtendedPlugin owner;

    @ImplConst
    private UserCacheImpl(ExtendedPlugin owner, SqlEngine engine, SqlTable table) {
        super(engine, table);
        this.owner = owner;
        this.owner.registerEvents(this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void handle(PlayerLoginEvent e) {
        owner.async(() -> cache(e.getPlayer()));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void handle(PlayerQuitEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    public void cache(Player proxiedPlayer) {
        cache(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());
    }


}
