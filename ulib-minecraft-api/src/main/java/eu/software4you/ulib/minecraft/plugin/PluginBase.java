package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.ulib.minecraft.plugin.controllers.EventController;
import eu.software4you.ulib.minecraft.plugin.controllers.SchedulerController;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;

/**
 * The base for plugins.
 *
 * @param <L> Listener type
 * @param <R> Scheduler return type
 * @param <E> Layout receiver
 */
public interface PluginBase<L, R, E> extends EventController<L>, SchedulerController<R> {
    String LAYOUT_BASE_NAME = "layout";
    String LAYOUT_FILE_EXTENSION = "yml";
    String DEFAULT_LAYOUT_FILE_NAME = String.format("%s.%s", LAYOUT_BASE_NAME, LAYOUT_FILE_EXTENSION);

    /**
     * Returns the name of the plugin.
     *
     * @return the name of the plugin.
     */
    String getName();

    /**
     * Attempts to save the default {@code config.yml} file, if the plugin contains one.
     */
    void saveDefaultConfig();

    /**
     * Returns the configuration wrapper for the {@code config.yml} file, if there is one.<br>
     * Attempts to save the default {@code config.yml} file.
     *
     * @return the config wrapper
     */
    @NotNull ConfigurationWrapper getConf();

    /**
     * Reloads the config wrapper.<br>
     * <b>Does not</b> reload the framework's config instance, if there is one.
     */
    void reloadConfig();

    /**
     * Attempts to save the default {@code layout.yml} file, if the plugin contains one.
     */
    void saveDefaultLayout();

    /**
     * Returns the layout instance for the {@code layout.yml} file, if there is one.<br>
     * Attempts to save the default {@code layout.yml} file.
     *
     * @return the layout instance
     */
    @NotNull Layout<E> getLayout();

    /**
     * Reloads the layout.
     */
    void reloadLayout();

    /**
     * Sets the locale of the layout.<br>
     * Reloads the layout afterwards.
     * <p>
     * Effectively uses another layout file name: {@code layout.{@link Locale#getLanguage()}.yml}
     * </p>
     * Use {@code null} as param to go back to the default layout file.
     *
     * @param locale the locale to set, or {@code null} to reset to the default layout
     */
    void setLayoutLocale(Locale locale);

    /**
     * Returns the data folder of the plugin.
     *
     * @return the data folder of the plugin
     */
    File getDataFolder();
}
