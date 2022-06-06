package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.minecraft.plugin.controllers.ASyncSchedulerController;
import eu.software4you.ulib.minecraft.plugin.controllers.EventController;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The base for plugins.
 *
 * @param <L> listener type
 * @param <R> scheduler task object return type
 */
public interface PluginBase<L, R> extends EventController<L>, ASyncSchedulerController<R> {
    /**
     * This method returns the plugin object. Can be useful if this object is only a substitute.
     *
     * @return the plugin object
     */
    @NotNull
    Object getPluginObject();

    /**
     * Returns the name of the plugin.
     *
     * @return the name of the plugin.
     */
    @NotNull
    String getName();

    /**
     * Attempts to save the default {@code config.yml} file, if the plugin contains one.
     */
    void saveDefaultConfig();

    /**
     * Returns the an extended sub instance for the {@code config.yml} file, if there is one.<br>
     * Attempts to save the default {@code config.yml} file.
     *
     * @return the sub
     */
    @NotNull
    YamlConfiguration getConf();

    /**
     * Reloads the extended sub retrieved with {@link #getConf()} with the {@code config.yml} file.<br>
     * <b>Does not</b> reload the framework's config instance, if there is one.
     */
    void reloadConfig();

    /**
     * Returns the data folder of the plugin.
     *
     * @return the data folder of the plugin
     */
    @NotNull
    Path getDataDir();

    /**
     * Returns the plugin's location in the file system.
     *
     * @return the location
     */
    @NotNull
    Path getLocation();
}
