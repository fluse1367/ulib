package eu.software4you.ulib;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import eu.software4you.velocity.plugin.VelocityPlugin;

class UserCacheImpl extends UserCache {
    private final VelocityPlugin owner;

    protected UserCacheImpl(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
        super(sqlEngine, table);
        if (!(owner instanceof VelocityPlugin))
            throw new IllegalStateException("Implementation of wrong type.");
        this.owner = (VelocityPlugin) owner;
        this.owner.registerEvents(this);
    }

    @Subscribe(order = PostOrder.FIRST)
    public void handle(PostLoginEvent e) {
        owner.async(() -> cache(e.getPlayer()));
    }


    @Subscribe(order = PostOrder.LAST)
    public void handle(DisconnectEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    public void cache(Player player) {
        cache(player.getUniqueId(), player.getUsername());
    }
}
