package eu.software4you.proxy.plugin;


import net.md_5.bungee.api.plugin.Listener;

public interface ProxyEventController {
    void registerEvents(Listener listener);

    void unregisterEvents(Listener listener);

    void unregisterAllEvents();
}
