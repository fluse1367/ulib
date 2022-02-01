package eu.software4you.ulib.impl.spigot.inventorymenu;

import eu.software4you.spigot.inventorymenu.MenuManager;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.ulib.inject.ImplConst;
import org.bukkit.plugin.Plugin;

@Impl(MenuManager.class)
final class MenuManagerImpl extends MenuManager {
    @ImplConst
    private MenuManagerImpl(Plugin plugin) {
        super(plugin);
        handler = new MenuManagerListener(this);
    }
}
