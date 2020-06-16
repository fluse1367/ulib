package eu.software4you.minecraft.plugin;

import eu.software4you.configuration.SimpleConfigurationWrapper;
import eu.software4you.configuration.SimpleLayoutWrapper;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;
import ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class ExtendedJavaPlugin extends JavaPlugin implements ExtendedPlugin {
    private final SimpleLayoutWrapper layoutWrapper = new SimpleLayoutWrapper(null);
    private final SimpleConfigurationWrapper configWrapper = new SimpleConfigurationWrapper(null);

    public ExtendedJavaPlugin() {
        super();
    }

    public ExtendedJavaPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public final SimpleConfigurationWrapper getConf() {
        if (configWrapper.section() == null)
            reloadConfig();
        return configWrapper;
    }

    @Override
    public final void reloadConfig() {
        saveDefaultConfig();
        configWrapper.setSection(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml")));
    }

    @Override
    public final void saveDefaultLayout() {
        if (!new File(getDataFolder(), "layout.yml").exists())
            saveResource("layout.yml", false);
    }

    @Override
    public final SimpleLayoutWrapper getLayout() {
        if (layoutWrapper.section() == null)
            reloadLayout();
        return layoutWrapper;
    }

    @Override
    public final void reloadLayout() {
        saveDefaultLayout();
        layoutWrapper.setSection(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "layout.yml")));
    }

    @Override
    public final BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public final BukkitTask sync(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay);
    }

    @Override
    public final BukkitTask sync(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period);
    }

    @Override
    public final BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public final BukkitTask async(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay);
    }

    @Override
    public final BukkitTask async(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period);
    }

    @Override
    public final void cancelAllTasks() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public final void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public final void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public final void unregisterAllEvents() {
        HandlerList.unregisterAll(this);
    }
}
