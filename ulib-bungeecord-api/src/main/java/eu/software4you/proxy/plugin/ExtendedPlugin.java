package eu.software4you.proxy.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import eu.software4you.proxy.configuration.Layout;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Locale;

public abstract class ExtendedPlugin extends Plugin implements ProxySchedulerController, ProxyEventController, ProxyCommandController {
    public abstract void saveDefaultConfig();

    public abstract ConfigurationWrapper getConfig();

    public abstract void reloadConfig();

    public abstract void saveDefaultLayout();

    public abstract void saveResource(String resourcePath, boolean replace);

    public abstract Layout getLayout();

    public abstract void reloadLayout();

    public abstract void setLayoutLocale(Locale locale);
}
