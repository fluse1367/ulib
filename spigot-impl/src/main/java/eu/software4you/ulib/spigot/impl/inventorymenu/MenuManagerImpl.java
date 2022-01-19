package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.api.internal.Providers;
import eu.software4you.ulib.spigot.api.inventorymenu.MenuManager;
import org.bukkit.plugin.Plugin;

public final class MenuManagerImpl extends MenuManager {

    private MenuManagerImpl(Plugin plugin) {
        super(plugin);
        handler = new MenuManagerListener(this);
    }

    public static class ProviderMenuManager implements Providers.ProviderMenuManager {

        @Override
        public MenuManager provide(Plugin plugin) {
            return new MenuManagerImpl(plugin);
        }
    }
}
