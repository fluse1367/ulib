package eu.software4you.ulib.velocity.impl.usercache;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.minecraft.api.internal.Providers;
import eu.software4you.ulib.minecraft.api.plugin.PluginBase;
import eu.software4you.ulib.minecraft.impl.usercache.UserCache;
import eu.software4you.ulib.velocity.api.plugin.VelocityPlugin;

public final class UserCacheImpl extends UserCache {
    private final VelocityPlugin owner;

    UserCacheImpl(VelocityPlugin owner, SqlEngine engine, SqlTable table) {
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

    public static final class UserCacheProvider implements Providers.ProviderUserCache {
        @Override
        public eu.software4you.ulib.minecraft.api.usercache.UserCache provide(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
            return new UserCacheImpl((VelocityPlugin) owner, sqlEngine, table);
        }
    }

}
