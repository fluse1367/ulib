package eu.software4you.ulib.impl.velocity.usercache;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.inject.Factory;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import eu.software4you.velocity.plugin.VelocityPlugin;

@Impl(UserCache.class)
public final class UserCacheImpl extends eu.software4you.ulib.impl.minecraft.usercache.UserCache {
    private final VelocityPlugin owner;

    @Factory
    private UserCacheImpl(VelocityPlugin owner, SqlEngine engine, SqlTable table) {
        super(engine, table);
        this.owner = owner;
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
