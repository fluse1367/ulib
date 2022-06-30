package eu.software4you.ulib.velocity.impl.usercache;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import eu.software4you.ulib.core.database.sql.Table;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import eu.software4you.ulib.minecraft.plugin.PluginBase;

public final class UserCacheImpl extends AbstractUserCache {
    private final PluginBase<Object, ?> owner;

    public UserCacheImpl(PluginBase<?, ?> pl, Table table) {
        super(table);

        //noinspection unchecked
        this.owner = (PluginBase<Object, ?>) pl;
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
