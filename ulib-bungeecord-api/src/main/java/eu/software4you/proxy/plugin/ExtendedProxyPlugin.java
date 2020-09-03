package eu.software4you.proxy.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.proxy.configuration.LayoutWrapper;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

public abstract class ExtendedProxyPlugin extends ExtendedPlugin {
    private final ConfigurationWrapper configWrapper = new ConfigurationWrapper(null);
    private final LayoutWrapper layoutWrapper = new LayoutWrapper(null);

    @Override
    public final void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
    }

    @Override
    public final ConfigurationWrapper getConfig() {
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
    public final LayoutWrapper getLayout() {
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
    public final void saveResource(String resourcePath, boolean replace) {
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
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public final ScheduledTask async(Runnable runnable) {
        return getProxy().getScheduler().runAsync(this, runnable);
    }

    @Override
    public final ScheduledTask async(Runnable runnable, long delay, TimeUnit unit) {
        return getProxy().getScheduler().schedule(this, runnable, delay, unit);
    }

    @Override
    public final ScheduledTask async(Runnable runnable, long delay, long period, TimeUnit unit) {
        return getProxy().getScheduler().schedule(this, runnable, delay, period, unit);
    }

    @Override
    public final void cancelAllTasks() {
        getProxy().getScheduler().cancel(this);
    }

    @Override
    public final void registerEvents(Listener listener) {
        getProxy().getPluginManager().registerListener(this, listener);
    }

    @Override
    public final void unregisterEvents(Listener listener) {
        getProxy().getPluginManager().unregisterListener(listener);
    }

    @Override
    public final void unregisterAllEvents() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    @Override
    public final void registerCommand(Command command) {
        getProxy().getPluginManager().registerCommand(this, command);
    }

    @Override
    public final void unregisterCommand(Command command) {
        getProxy().getPluginManager().unregisterCommand(command);
    }

    @Override
    public final void unregisterAllCommands() {
        getProxy().getPluginManager().unregisterCommands(this);
    }
}
