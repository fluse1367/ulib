package eu.software4you.ulib.spigot.impl.usercache;

import eu.software4you.ulib.core.database.sql.Table;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.spigot.impl.DelegationListener;
import eu.software4you.ulib.spigot.plugin.ExtendedPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class UserCacheImpl extends AbstractUserCache {
    private final ExtendedPlugin owner;

    public UserCacheImpl(PluginBase<?, ?> pl, Table table) {
        super(table);

        if (!(pl instanceof ExtendedPlugin owner))
            throw new IllegalArgumentException("Plugin not an instance of ExtendedPlugin");

        this.owner = owner;

        var d = new DelegationListener();
        DelegationListener.registerDelegation(d, PlayerLoginEvent.class, this::handle, EventPriority.LOWEST, false, owner);
        DelegationListener.registerDelegation(d, PlayerQuitEvent.class, this::handle, EventPriority.HIGHEST, false, owner);
    }

    public void handle(PlayerLoginEvent e) {
        owner.async(() -> cache(e.getPlayer()));
    }

    public void handle(PlayerQuitEvent e) {
        cache.remove(e.getPlayer().getUniqueId());
    }

    public void cache(Player proxiedPlayer) {
        cache(proxiedPlayer.getUniqueId(), proxiedPlayer.getName());
    }
}
