package eu.software4you.ulib.spigot.api.internal;

import eu.software4you.ulib.spigot.api.inventorymenu.MenuManager;
import eu.software4you.ulib.spigot.api.plugin.Layout;
import org.bukkit.plugin.Plugin;

import java.util.ServiceLoader;

public final class Providers {
    public interface ProviderMenuManager {
        MenuManager provide(Plugin plugin);
    }

    public interface ProviderLayout extends ServiceLoader.Provider<Layout> {
    }
}
