package eu.software4you.ulib.velocity.plugin;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

/**
 * Support class for velocity plugins.
 *
 * @see VelocityJavaPlugin
 */
public interface VelocityPlugin extends PluginBase<Object, ScheduledTask> {

    /**
     * Returns the id of the plugin.
     *
     * @return the id
     */
    @NotNull
    String getId();

    /**
     * Returns the plugin's proxy instance.
     *
     * @return the proxy instance
     */
    @NotNull
    ProxyServer getProxyServer();

    /**
     * Returns the plugin's logger.
     *
     * @return the logger
     */
    @NotNull
    Logger getLogger();

    /**
     * Saves a resource that is embedded into the plugin's .jar file to the data folder.
     *
     * @param resourcePath the path of the embedded resource (without leading {@code /})
     * @param replace      {@code true}, if the resource should be overwritten if the file already exists
     * @see #getDataDir()
     */
    void saveResource(@NotNull String resourcePath, boolean replace);

    /**
     * @deprecated Velocity does officially not support batch task cancellation.
     * The implementation of this method achieves this through reflection and is thus <b>unstable</b>.
     */
    @Deprecated
    @Override
    void cancelAllTasks();
}
