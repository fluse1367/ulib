package eu.software4you.ulib.bungeecord.plugin;

import net.md_5.bungee.api.plugin.Event;

public class ExtendedPluginEvent extends Event {
    private final ExtendedPlugin plugin;

    public ExtendedPluginEvent(ExtendedPlugin plugin) {
        this.plugin = plugin;
    }

    public ExtendedPlugin getPlugin() {
        return plugin;
    }
}
