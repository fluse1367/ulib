package eu.software4you.ulib.impl.spigot.inventorymenu;

import eu.software4you.spigot.inventorymenu.MenuManager;
import eu.software4you.ulib.ImplRegistry;
import org.bukkit.plugin.Plugin;

public class MenuManagerImpl extends MenuManager {

    static {
        ImplRegistry.put(MenuManager.class, MenuManagerImpl::new);
    }

    private MenuManagerImpl(Object[] params) {
        super((Plugin) params[0]);
        handler = new MenuManagerListener(this);
    }
}
