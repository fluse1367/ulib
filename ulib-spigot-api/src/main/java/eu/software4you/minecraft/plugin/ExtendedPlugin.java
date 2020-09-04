package eu.software4you.minecraft.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.configuration.Layout;
import org.bukkit.plugin.Plugin;

import java.util.Locale;

public interface ExtendedPlugin extends Plugin, BukkitSchedulerController, BukkitEventController {
    ConfigurationWrapper getConf();

    void saveDefaultLayout();

    Layout getLayout();

    void reloadLayout();

    void setLayoutLocale(Locale locale);
}
