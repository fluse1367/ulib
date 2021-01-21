package eu.software4you.ulib;

import eu.software4you.spigot.plugin.ExtendedPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

class UserCacheImpl extends UserCache implements Listener {
    private final ExtendedPlugin owner;

    protected UserCacheImpl(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        super(sqlEngine, table);
        if (!(owner instanceof ExtendedPlugin))
            throw new IllegalStateException("Implementation of wrong type.");
        this.owner = (ExtendedPlugin) owner;
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
