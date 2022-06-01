package eu.software4you.ulib.bungeecord.plugin;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.core.io.IOUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Implementation of {@link ExtendedPlugin}.
 * <p>
 * You can use it by simply extending your plugin class with this class <i>instead</i> of {@link net.md_5.bungee.api.plugin.Plugin}:
 * <pre>{@code
 * // ...
 * public class YourBungeeCordPlugin extends ExtendedProxyPlugin {
 *     // ...
 * }
 * }</pre>
 */
public abstract class ExtendedProxyPlugin extends ExtendedPlugin {
    private final YamlConfiguration config = YamlConfiguration.newYaml();
    private boolean configInit;

    public ExtendedProxyPlugin() {
        super();
    }

    public ExtendedProxyPlugin(@NotNull ProxyServer proxy, @NotNull PluginDescription description) {
        super(proxy, description);
    }

    @Override
    public @NotNull Object getPluginObject() {
        return this;
    }

    @Override
    public void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
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
    public void saveResource(@NotNull String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getPluginObject().getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getFile().getPath());
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try (in) {
            if (!outFile.exists() || replace) {
                try (in; var out = new FileOutputStream(outFile)) {
                    IOUtil.write(in, out);
                }
            } else {
                getLogger().log(Level.WARNING, "Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            getLogger().log(Level.SEVERE, "Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public @NotNull ScheduledTask async(@NotNull Runnable runnable) {
        return getProxy().getScheduler().runAsync(this, runnable);
    }

    @Override
    public @NotNull ScheduledTask async(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return getProxy().getScheduler().schedule(this, runnable, delay, unit);
    }

    @Override
    public @NotNull ScheduledTask async(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit unit) {
        return getProxy().getScheduler().schedule(this, runnable, delay, period, unit);
    }

    @Override
    public void cancelAllTasks() {
        getProxy().getScheduler().cancel(this);
    }

    @Override
    public void registerEvents(@NotNull Listener listener) {
        getProxy().getPluginManager().registerListener(this, listener);
    }

    @Override
    public void unregisterEvents(@NotNull Listener listener) {
        getProxy().getPluginManager().unregisterListener(listener);
    }

    @Override
    public void unregisterAllEvents() {
        getProxy().getPluginManager().unregisterListeners(this);
    }

    @Override
    public void registerCommand(@NotNull Command command) {
        getProxy().getPluginManager().registerCommand(this, command);
    }

    @Override
    public void unregisterCommand(@NotNull Command command) {
        getProxy().getPluginManager().unregisterCommand(command);
    }

    @Override
    public void unregisterAllCommands() {
        getProxy().getPluginManager().unregisterCommands(this);
    }

    @Override
    public @NotNull ScheduledTask sync(@NotNull Runnable runnable) {
        throw new UnsupportedOperationException("Bungeecord does not provide synchronous tasks.");
    }

    @Override
    public @NotNull ScheduledTask sync(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        throw new UnsupportedOperationException("Bungeecord does not provide synchronous tasks.");
    }

    @Override
    public @NotNull ScheduledTask sync(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit unit) {
        throw new UnsupportedOperationException("Bungeecord does not provide synchronous tasks.");
    }
}
