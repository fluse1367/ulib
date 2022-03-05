package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.api.internal.Providers;
import eu.software4you.ulib.spigot.api.inventorymenu.MenuManager;
import eu.software4you.ulib.spigot.impl.combinedlisteners.DelegationListener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.plugin.Plugin;

public final class MenuManagerImpl extends MenuManager {

    private MenuManagerImpl(Plugin plugin) {
        super(plugin);

        var listener = new MenuManagerListener(this);
        super.handlerRegister = () -> {
            var d = DelegationListener.registerSingleDelegation(InventoryClickEvent.class, listener::handle);
            DelegationListener.registerDelegation(d, InventoryOpenEvent.class, listener::handle);
            DelegationListener.registerDelegation(d, InventoryCloseEvent.class, listener::handle);
            return d;
        };
        super.reset = () -> listener.getNoTriggerOpenClose().clear();
    }

    public static class MenuManagerProvider implements Providers.ProviderMenuManager {

        @Override
        public MenuManager provide(Plugin plugin) {
            return new MenuManagerImpl(plugin);
        }
    }
}
