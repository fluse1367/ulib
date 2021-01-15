package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.ulib.minecraft.plugin.controllers.EventController;
import eu.software4you.ulib.minecraft.plugin.controllers.SchedulerController;

import java.io.File;
import java.util.Locale;

/**
 * @param <L> Listener type
 * @param <R> Scheduler return type
 */
public interface PluginBase<L, R> extends EventController<L>, SchedulerController<R> {
    String LAYOUT_BASE_NAME = "layout";
    String LAYOUT_FILE_EXTENSION = "yml";
    String DEFAULT_LAYOUT_FILE_NAME = String.format("%s.%s", LAYOUT_BASE_NAME, LAYOUT_FILE_EXTENSION);

    String getName();

    void saveDefaultConfig();

    ConfigurationWrapper getConf();

    void reloadConfig();

    void saveDefaultLayout();

    Layout<?> getLayout();

    void reloadLayout();

    void setLayoutLocale(Locale locale);

    File getDataFolder();
}
