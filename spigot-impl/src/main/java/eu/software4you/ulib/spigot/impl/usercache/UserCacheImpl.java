package eu.software4you.ulib.spigot.impl.usercache;

import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.sql.SqlTable;
import eu.software4you.ulib.minecraft.api.internal.Providers;
import eu.software4you.ulib.minecraft.api.plugin.PluginBase;
import eu.software4you.ulib.minecraft.api.usercache.UserCache;
import eu.software4you.ulib.spigot.api.plugin.ExtendedPlugin;
import eu.software4you.ulib.spigot.impl.PluginSubst;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class UserCacheImpl extends eu.software4you.ulib.minecraft.impl.usercache.UserCache implements Listener {
    private final ExtendedPlugin owner;

    UserCacheImpl(ExtendedPlugin owner, SqlEngine engine, SqlTable table) {
        super(engine, table);
        PluginSubst.getInstance().makeBukkitAvailable();
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

    public static final class UserCacheProvider implements Providers.ProviderUserCache {
        @Override
        public UserCache provide(PluginBase<?, ?, ?> owner, SqlEngine sqlEngine, SqlTable table) {
            return new UserCacheImpl((ExtendedPlugin) owner, sqlEngine, table);
        }
    }

}
