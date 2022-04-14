package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.impl.DelegationListener;
import eu.software4you.ulib.spigot.inventorymenu.MenuManager;
import org.bukkit.event.inventory.*;
import org.bukkit.plugin.Plugin;

public final class MenuManagerImpl extends MenuManager {

    public MenuManagerImpl(Plugin plugin) {
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
}
