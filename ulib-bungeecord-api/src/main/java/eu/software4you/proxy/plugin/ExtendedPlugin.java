package eu.software4you.proxy.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.proxy.configuration.LayoutWrapper;
import net.md_5.bungee.api.plugin.Plugin;

public abstract class ExtendedPlugin extends Plugin implements ProxySchedulerController, ProxyEventController, ProxyCommandController {
    public abstract void saveDefaultConfig();

    public abstract ConfigurationWrapper getConfig();

    public abstract void reloadConfig();

    public abstract void saveDefaultLayout();

    public abstract void saveResource(String resourcePath, boolean replace);

    public abstract LayoutWrapper getLayout();

    public abstract void reloadLayout();
}
