package eu.software4you.ulib.spigot.plugin;

import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.plugin.controllers.SchedulerController;
import eu.software4you.ulib.spigot.inventorymenu.MenuManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Extended version of Bukkit's {@link Plugin}.
 *
 * @see ExtendedJavaPlugin
 */
public interface ExtendedPlugin extends Plugin, PluginBase<Listener, BukkitTask>, SchedulerController<BukkitTask> {
    @Override
    @NotNull Plugin getPluginObject();

    /**
     * Gets the main menu manager. Creates a new one if needed.
     *
     * @return the main menu manager
     */
    @NotNull
    MenuManager getMainMenuManager();

    /**
     * Creates a new menu manager.
     *
     * @return a new menu manager
     */
    @NotNull
    default MenuManager getNewMenuManager() {
        return MenuManager.of(this);
    }
}
