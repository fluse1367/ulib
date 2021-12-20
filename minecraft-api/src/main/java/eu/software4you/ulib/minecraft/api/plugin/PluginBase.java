package eu.software4you.ulib.minecraft.api.plugin;

import eu.software4you.ulib.core.api.configuration.yaml.ExtYamlSub;
import eu.software4you.ulib.minecraft.api.plugin.controllers.ASyncSchedulerController;
import eu.software4you.ulib.minecraft.api.plugin.controllers.EventController;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Locale;

/**
 * The base for plugins.
 *
 * @param <L> listener type
 * @param <R> scheduler task object return type
 * @param <E> layout receiver type
 */
public interface PluginBase<L, R, E> extends EventController<L>, ASyncSchedulerController<R> {
    String LAYOUT_BASE_NAME = "layout";
    String LAYOUT_FILE_EXTENSION = "yml";
    String DEFAULT_LAYOUT_FILE_NAME = String.format("%s.%s", LAYOUT_BASE_NAME, LAYOUT_FILE_EXTENSION);

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
    ExtYamlSub getConf();

    /**
     * Reloads the extended sub retrieved with {@link #getConf()} with the {@code config.yml} file.<br>
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
    @NotNull
    Layout<E> getLayout();

    /**
     * Reloads the layout.
     */
    void reloadLayout();

    /**
     * Sets the locale of the layout.<br>
     * Reloads the layout afterwards.
     * <p>
     * Effectively uses another layout file name: {@code layout.{@link Locale#getLanguage()}.yml}
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
    @NotNull
    File getDataFolder();
}
