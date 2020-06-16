package eu.software4you.minecraft.plugin;

import eu.software4you.configuration.SimpleConfigurationWrapper;
import eu.software4you.configuration.SimpleLayoutWrapper;
import org.bukkit.plugin.Plugin;

public interface ExtendedPlugin extends Plugin, BukkitSchedulerController, BukkitEventController {
    SimpleConfigurationWrapper getConf();

    void saveDefaultLayout();

    SimpleLayoutWrapper getLayout();

    void reloadLayout();
}
