package eu.software4you.ulib.spigot.plugin;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.spigot.inventorymenu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Implementation of {@link ExtendedPlugin}.
 * <p>
 * You can use it by simply extending your plugin class with this class <i>instead</i> of {@link JavaPlugin}:
 * <pre>{@code
 * // ...
 * public class YourSpigotPlugin extends ExtendedJavaPlugin {
 *     // ...
 * }
 * }</pre>
 */
public abstract class ExtendedJavaPlugin extends JavaPlugin implements ExtendedPlugin {
    private final YamlConfiguration config = YamlConfiguration.newYaml();
    private MenuManager mainMenuManager;
    private boolean configInit;

    public ExtendedJavaPlugin() {
        super();
    }

    public ExtendedJavaPlugin(@NotNull JavaPluginLoader loader, @NotNull PluginDescriptionFile description,
                              @NotNull File dataFolder, @NotNull File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public @NotNull Path getDataDir() {
        return getDataFolder().toPath();
    }

    @Override
    public @NotNull Path getLocation() {
        return getFile().toPath();
    }

    @Override
    public @NotNull Plugin getPluginObject() {
        return this;
    }

    @Override
    public @NotNull YamlConfiguration getConf() {
        if (!configInit) {
            reloadConfig();
            configInit = true;
        }
        return config;
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        try {
            config.reinit(new FileReader(new File(getDataFolder(), "config.yml")));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, e, () -> "Failure while reloading config.yml!");
        }
    }

    @Override
    public @NotNull BukkitTask sync(@NotNull Runnable runnable) {
        return Bukkit.getScheduler().runTask(getPluginObject(), runnable);
    }

    @Override
    public @NotNull BukkitTask sync(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return sync(runnable, unit.toMillis(delay) / 50);
    }

    @Override
    public @NotNull BukkitTask sync(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit unit) {
        return sync(runnable, unit.toMillis(delay) / 50, unit.toMillis(period) / 50);
    }

    @Override
    public @NotNull BukkitTask sync(@NotNull Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(getPluginObject(), runnable, delay);
    }

    @Override
    public @NotNull BukkitTask sync(@NotNull Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(getPluginObject(), runnable, delay, period);
    }

    @Override
    public @NotNull BukkitTask async(@NotNull Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(getPluginObject(), runnable);
    }

    @Override
    public @NotNull BukkitTask async(@NotNull Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(getPluginObject(), runnable, delay, period);
    }

    @Override
    public @NotNull BukkitTask async(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return async(runnable, unit.toMillis(delay) / 50);
    }

    @Override
    public @NotNull BukkitTask async(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit unit) {
        return async(runnable, unit.toMillis(delay) / 50, unit.toMillis(period) / 50);
    }

    @Override
    public @NotNull BukkitTask async(@NotNull Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(getPluginObject(), runnable, delay);
    }

    @Override
    public void cancelAllTasks() {
        Bukkit.getScheduler().cancelTasks(getPluginObject());
    }

    @Override
    public void registerEvents(@NotNull Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, getPluginObject());
    }

    @Override
    public void unregisterEvents(@NotNull Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void unregisterAllEvents() {
        HandlerList.unregisterAll(getPluginObject());
    }

    @Override
    public @NotNull MenuManager getMainMenuManager() {
        if (mainMenuManager == null)
            mainMenuManager = getNewMenuManager();
        return mainMenuManager;
    }
}
