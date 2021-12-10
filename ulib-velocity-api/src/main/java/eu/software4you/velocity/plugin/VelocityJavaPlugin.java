package eu.software4you.velocity.plugin;

import com.google.common.collect.Multimap;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import eu.software4you.ulib.core.api.configuration.Configurations;
import eu.software4you.ulib.core.api.configuration.yaml.ExtYamlSub;
import eu.software4you.ulib.core.api.io.IOUtil;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ImplFactory;
import eu.software4you.ulib.core.api.utils.FileUtils;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.*;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of {@link VelocityPlugin}.
 * <p>
 * Because velocity does not work with plugin interfaces it is necessary to manually implement this class and call the constructor:
 * <pre>{@code
 * // ...
 * @Plugin(id = "plugin_id")
 * public class YourVelocityPlugin extends VelocityJavaPlugin {
 *     // ...
 *     @Inject
 *     public YourVelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataPath) {
 *         // plugin id, proxy server, logger, data dir
 *         super("plugin_id", proxyServer, logger, dataPath.toFile());
 *     }
 *     // ...
 * }
 * // ...
 * }</pre>
 * <b>It is crucial to pass the exact <u>same</u> plugin id to the superclass constructor.</b>
 */
@RequiredArgsConstructor
public abstract class VelocityJavaPlugin implements VelocityPlugin {
    private final static String layoutBaseName = "layout";
    private final static String layoutFileExtension = "yml";
    private final static String defaultLayoutFileName = String.format("%s.%s", layoutBaseName, layoutFileExtension);
    @Await
    private static ImplFactory<Layout> layoutFactory;
    @Getter
    @NotNull
    private final String id;
    @Getter
    @NotNull
    private final ProxyServer proxyServer;
    @Getter
    @NotNull
    private final Logger logger;
    @Getter
    @NotNull
    private final File dataFolder;
    @Getter
    private final File file = FileUtils.getClassFile(getClass());
    private final ExtYamlSub config = (ExtYamlSub) Configurations.newYaml();
    private final Layout layout = layoutFactory.fabricate();
    private String layoutFileName = defaultLayoutFileName;
    private boolean configInit, layoutInit;

    private PluginContainer getPlugin() {
        return proxyServer.getPluginManager().getPlugin(id)
                .orElseThrow(() -> new IllegalStateException("Invalid Plugin ID"));
    }

    @Override
    public @NotNull String getName() {
        return getPlugin().getDescription().getName().orElse("");
    }

    @Override
    public void saveDefaultConfig() {
        if (!new File(getDataFolder(), "config.yml").exists())
            saveResource("config.yml", false);
    }

    @Override
    public @NotNull ExtYamlSub getConf() {
        if (!configInit) {
            configInit = true;
            reloadConfig();
        }
        return config;
    }

    @Override
    public void reloadConfig() {
        saveDefaultConfig();
        try {
            config.load(new FileReader(new File(getDataFolder(), "config.yml")));
        } catch (IOException e) {
            getLogger().warn("Failure while reloading config.yml!", e);
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
            layoutInit = true;
            reloadLayout();
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
            getLogger().warn(String.format("Failure while reloading %s!", layoutFile.getName()), e);
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
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
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
                logger.warn("Could not save " + outFile.getName() + " to " + outFile + " because " + outFile.getName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.error("Could not save " + outFile.getName() + " to " + outFile, ex);
        }
    }

    @Override
    public ScheduledTask async(Runnable runnable) {
        return proxyServer.getScheduler().buildTask(this, runnable).schedule();
    }

    @Override
    public ScheduledTask async(Runnable runnable, long delay, TimeUnit unit) {
        return proxyServer.getScheduler().buildTask(this, runnable)
                .delay(delay, unit)
                .schedule();
    }

    @Override
    public ScheduledTask async(Runnable runnable, long delay, long period, TimeUnit unit) {
        return proxyServer.getScheduler().buildTask(this, runnable)
                .delay(delay, unit)
                .repeat(period, unit)
                .schedule();
    }

    @SneakyThrows
    @Override
    public void cancelAllTasks() {
        ((Multimap<Object, ScheduledTask>) ReflectUtil.forceCall(getProxyServer().getScheduler().getClass(),
                proxyServer.getScheduler(), "tasksByPlugin")).get(this).forEach(ScheduledTask::cancel);
    }

    @Override
    public void registerEvents(Object listener) {
        proxyServer.getEventManager().register(this, listener);
    }

    @Override
    public void unregisterEvents(Object listener) {
        proxyServer.getEventManager().unregisterListener(this, listener);
    }

    @Override
    public void unregisterAllEvents() {
        proxyServer.getEventManager().unregisterListeners(this);
    }
}
