package eu.software4you.ulib.velocity.plugin;

import com.google.common.collect.Multimap;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Conversions;
import eu.software4you.ulib.core.util.FileUtil;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private final Path dataDir;
    @Getter
    @NotNull
    private final Path location = FileUtil.getClassFile(getClass()).getValue().toPath();
    private final YamlConfiguration config = YamlConfiguration.newYaml();
    private boolean configInit;

    private PluginContainer getPlugin() {
        return proxyServer.getPluginManager().getPlugin(id)
                .orElseThrow(() -> new IllegalStateException("Invalid Plugin ID"));
    }

    @Override
    public @NotNull Object getPluginObject() {
        return this;
    }

    @Override
    public @NotNull String getName() {
        return getPlugin().getDescription().getName().orElse("");
    }

    @Override
    public void saveDefaultConfig() {
        if (!Files.exists(getDataDir().resolve("config.yml")))
            saveResource("config.yml", false);
    }

    @Override
    public @NotNull YamlConfiguration getConf() {
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
            config.reinitFrom(getDataDir().resolve("config.yml"))
                    .rethrow(IOException.class);
        } catch (IOException e) {
            getLogger().warn("Failure while reloading config.yml!", e);
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
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found in " + getLocation());
        }


        Path dest = getDataDir().resolve(resourcePath);

        try {
            Files.createDirectories(dest.getParent());

            if (!Files.exists(dest) || replace) {
                try (in; var out = Files.newOutputStream(dest)) {
                    IOUtil.write(in, out);
                }
            } else {
                logger.warn("Could not save " + dest.getFileName() + " to " + dest + " because " + dest.getFileName() + " already exists.");
            }
        } catch (IOException ex) {
            logger.error("Could not save " + dest.getFileName() + " to " + dest, ex);
        }
    }

    @Override
    public @NotNull ScheduledTask async(@NotNull Runnable runnable) {
        return proxyServer.getScheduler().buildTask(getPluginObject(), runnable).schedule();
    }

    @Override
    public @NotNull ScheduledTask async(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit) {
        return proxyServer.getScheduler().buildTask(getPluginObject(), runnable)
                .delay(delay, unit)
                .schedule();
    }

    @Override
    public @NotNull ScheduledTask async(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit unit) {
        return proxyServer.getScheduler().buildTask(getPluginObject(), runnable)
                .delay(delay, unit)
                .repeat(period, unit)
                .schedule();
    }

    @SneakyThrows
    @Override
    public void cancelAllTasks() {
        var sch = proxyServer.getScheduler();
        //noinspection RedundantCast
        ReflectUtil.icall(Multimap.class, sch, "tasksByPlugin")
                .map(Multimap::values)
                .map(c -> Conversions.safecast(ScheduledTask.class, c).orElse(null))
                .ifPresentOrElse(tasks -> tasks.forEach(ScheduledTask::cancel), thr -> thr
                        .ifPresent(t -> logger.warn("Could not cancel tasks", (Throwable) t)));
    }

    @Override
    public void registerEvents(@NotNull Object listener) {
        proxyServer.getEventManager().register(getPluginObject(), listener);
    }

    @Override
    public void unregisterEvents(@NotNull Object listener) {
        proxyServer.getEventManager().unregisterListener(getPluginObject(), listener);
    }

    @Override
    public void unregisterAllEvents() {
        proxyServer.getEventManager().unregisterListeners(getPluginObject());
    }
}
