package eu.software4you.ulib.impl.velocity.usercache;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import eu.software4you.sql.SqlEngine;
import eu.software4you.sql.SqlTable;
import eu.software4you.ulib.ImplRegistry;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import eu.software4you.velocity.plugin.VelocityPlugin;

public final class UserCacheImpl extends UserCache {
    static {
        ImplRegistry.put(UserCache.class, UserCacheImpl::new);
    }

    private final VelocityPlugin owner;

    private UserCacheImpl(Object[] params) {
        super((SqlEngine) params[1], (SqlTable) params[2]);
        this.owner = (VelocityPlugin) params[0];
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
