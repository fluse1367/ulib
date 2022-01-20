package eu.software4you.ulib.bungeecord.api.plugin;

import eu.software4you.ulib.bungeecord.api.internal.Providers.ProviderLayout;
import eu.software4you.ulib.core.api.configuration.yaml.ExtYamlSub;
import eu.software4you.ulib.core.api.configuration.yaml.YamlSub;
import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.core.api.io.IOUtil;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Locale;
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
    private final static String layoutBaseName = "layout";
    private final static String layoutFileExtension = "yml";
    private final static String defaultLayoutFileName = String.format("%s.%s", layoutBaseName, layoutFileExtension);
    private final ExtYamlSub config = YamlSub.newYaml();
    private final Layout layout = Providers.get(ProviderLayout.class).get();
    private String layoutFileName = defaultLayoutFileName;
    private boolean configInit, layoutInit;

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
    public @NotNull ExtYamlSub getConf() {
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
            config.load(new FileReader(new File(getDataFolder(), "config.yml")));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, e, () -> "Failure while reloading config.yml!");
        }
    }

    @Override
    public void saveDefaultLayout() {
        if (!new File(getDataFolder(), layoutFileName).exists())
            saveResource(layoutFileName, false);
    }

    @Override
    public @NotNull Layout getLayout() {
        if (!layoutInit) {
            reloadLayout();
            layoutInit = true;
        }
        return layout;
    }

    @Override
    public void reloadLayout() {
        saveDefaultLayout();
        File layoutFile = new File(getDataFolder(), layoutFileName);
        try {
            layout.load(new FileReader(layoutFile));
        } catch (IOException e) {
            getLogger().log(Level.WARNING, e, () -> String.format("Failure while reloading %s!", layoutFile.getName()));
        }
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
