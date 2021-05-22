package eu.software4you.spigot.plugin;

import eu.software4you.spigot.inventorymenu.MenuManager;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public interface ExtendedPlugin extends Plugin, PluginBase<Listener, BukkitTask, CommandSender> {
    @Override
    SpigotLayout getLayout();

    /**
     * Gets the main menu manager. Creates a new one if needed.
     *
     * @return the main menu manager
     */
    MenuManager getMainMenuManager();

    /**
     * Creates a new menu manager.
     *
     * @return a new menu manager
     */
    default MenuManager getNewMenuManager() {
        return MenuManager.of(this);
    }
}
