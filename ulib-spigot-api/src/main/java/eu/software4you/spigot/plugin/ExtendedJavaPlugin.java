package eu.software4you.spigot.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.spigot.inventorymenu.MenuManager;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.scheduler.BukkitTask;
import ulib.ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public abstract class ExtendedJavaPlugin extends JavaPlugin implements ExtendedPlugin {
    private final SpigotLayout layout = new SpigotLayout(null);
    private final ConfigurationWrapper configWrapper = new ConfigurationWrapper(null);
    private String layoutFileName = DEFAULT_LAYOUT_FILE_NAME;
    private MenuManager mainMenuManager;

    public ExtendedJavaPlugin() {
        super();
    }

    public ExtendedJavaPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
    }

    @Override
    public ConfigurationWrapper getConf() {
        if (configWrapper.section() == null)
            reloadConfig();
        return configWrapper;
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        configWrapper.setSection(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml")));
    }

    @Override
    public void saveDefaultLayout() {
        if (!new File(getDataFolder(), layoutFileName).exists())
            saveResource(layoutFileName, false);
    }

    @Override
    public SpigotLayout getLayout() {
        if (layout.section() == null)
            reloadLayout();
        return layout;
    }

    @Override
    public void reloadLayout() {
        saveDefaultLayout();
        File layoutFile = new File(getDataFolder(), layoutFileName);
        layout.setSection(YamlConfiguration.loadConfiguration(layoutFile));
    }

    @Override
    public void setLayoutLocale(Locale locale) {
        if (locale == null || locale.getLanguage().isEmpty()) {
            layoutFileName = DEFAULT_LAYOUT_FILE_NAME;
        } else {
            layoutFileName = String.format("%s.%s.%s", LAYOUT_BASE_NAME, locale.getLanguage(), LAYOUT_FILE_EXTENSION);
        }
        reloadLayout();
    }

    @Override
    public BukkitTask sync(Runnable runnable) {
        return Bukkit.getScheduler().runTask(this, runnable);
    }

    @Override
    public BukkitTask sync(Runnable runnable, long delay, TimeUnit unit) {
        return sync(runnable, unit.toMillis(delay) / 50);
    }

    @Override
    public BukkitTask sync(Runnable runnable, long delay, long period, TimeUnit unit) {
        return sync(runnable, unit.toMillis(delay) / 50, unit.toMillis(period) / 50);
    }

    @Override
    public BukkitTask sync(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLater(this, runnable, delay);
    }

    @Override
    public BukkitTask sync(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimer(this, runnable, delay, period);
    }

    @Override
    public BukkitTask async(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public BukkitTask async(Runnable runnable, long delay, long period) {
        return Bukkit.getScheduler().runTaskTimerAsynchronously(this, runnable, delay, period);
    }

    @Override
    public BukkitTask async(Runnable runnable, long delay, TimeUnit unit) {
        return async(runnable, unit.toMillis(delay) / 50);
    }

    @Override
    public BukkitTask async(Runnable runnable, long delay, long period, TimeUnit unit) {
        return async(runnable, unit.toMillis(delay) / 50, unit.toMillis(period) / 50);
    }

    @Override
    public BukkitTask async(Runnable runnable, long delay) {
        return Bukkit.getScheduler().runTaskLaterAsynchronously(this, runnable, delay);
    }

    @Override
    public void cancelAllTasks() {
        Bukkit.getScheduler().cancelTasks(this);
    }

    @Override
    public void registerEvents(Listener listener) {
        Bukkit.getPluginManager().registerEvents(listener, this);
    }

    @Override
    public void unregisterEvents(Listener listener) {
        HandlerList.unregisterAll(listener);
    }

    @Override
    public void unregisterAllEvents() {
        HandlerList.unregisterAll(this);
    }

    @Override
    public MenuManager getMainMenuManager() {
        if (mainMenuManager == null)
            mainMenuManager = getNewMenuManager();
        return mainMenuManager;
    }
}
