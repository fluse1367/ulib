package eu.software4you.bungeecord.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.ulib.minecraft.plugin.Layout;
import eu.software4you.utils.IOUtil;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import ulib.ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class ExtendedProxyPlugin extends ExtendedPlugin {
    private final static String layoutBaseName = "layout";
    private final static String layoutFileExtension = "yml";
    private final static String defaultLayoutFileName = String.format("%s.%s", layoutBaseName, layoutFileExtension);
    private final ConfigurationWrapper configWrapper = new ConfigurationWrapper(null);
    private final Layout<CommandSender> layout = new BungeecordLayout(null);
    private String layoutFileName = defaultLayoutFileName;

    @Override
    public void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
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
    public Layout<CommandSender> getLayout() {
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
            layoutFileName = defaultLayoutFileName;
        } else {
            layoutFileName = String.format("%s.%s.%s", layoutBaseName, locale.getLanguage(), layoutFileExtension);
        }
        reloadLayout();
    }

    @Override
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile().getPath());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                IOUtil.write(in, new FileOutputStream(outFile));
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public ScheduledTask async(Runnable runnable) {
        return getProxy().getScheduler().runAsync(this, runnable);
    }

    @Override
    public ScheduledTask async(Runnable runnable, long delay, TimeUnit unit) {
        return getProxy().getScheduler().schedule(this, runnable, delay, unit);
    }

    @Override
    public ScheduledTask async(Runnable runnable, long delay, long period, TimeUnit unit) {
        return getProxy().getScheduler().schedule(this, runnable, delay, period, unit);
    }

    @Override
    public void cancelAllTasks() {
        getProxy().getScheduler().cancel(this);
    }

    @Override
    public void registerEvents(Listener listener) {
        getProxy().getPluginManager().registerListener(this, listener);
    }

    @Override
    public void unregisterEvents(Listener listener) {
        getProxy().getPluginManager().unregisterListener(listener);
    }

    @Override
    public void unregisterAllEvents() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    @Override
    public void registerCommand(Command command) {
        getProxy().getPluginManager().registerCommand(this, command);
    }

    @Override
    public void unregisterCommand(Command command) {
        getProxy().getPluginManager().unregisterCommand(command);
    }

    @Override
    public void unregisterAllCommands() {
        getProxy().getPluginManager().unregisterCommands(this);
    }

    @Override
    public ScheduledTask sync(Runnable runnable) {
        throw new UnsupportedOperationException("Bungeecord does not provide synchronous tasks.");
    }

    @Override
    public ScheduledTask sync(Runnable runnable, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException("Bungeecord does not provide synchronous tasks.");
    }

    @Override
    public ScheduledTask sync(Runnable runnable, long delay, long period, TimeUnit unit) {
        throw new UnsupportedOperationException("Bungeecord does not provide synchronous tasks.");
    }
}
