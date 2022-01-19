package eu.software4you.ulib.spigot.api.plugin;

import eu.software4you.ulib.minecraft.api.plugin.PluginBase;
import eu.software4you.ulib.minecraft.api.plugin.controllers.SchedulerController;
import eu.software4you.ulib.spigot.api.inventorymenu.MenuManager;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

/**
 * Extended version of Bukkit's {@link Plugin}.
 *
 * @see ExtendedJavaPlugin
 */
public interface ExtendedPlugin extends Plugin, PluginBase<Listener, BukkitTask, CommandSender>, SchedulerController<BukkitTask> {
    @Override
    @NotNull
    Layout getLayout();

    @Override
    @NotNull Plugin getPluginObject();

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
