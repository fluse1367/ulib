package eu.software4you.ulib.bungeecord.plugin;

import net.md_5.bungee.api.plugin.Event;
import org.jetbrains.annotations.NotNull;

public class ExtendedPluginEvent extends Event {
    private final ExtendedPlugin plugin;

    public ExtendedPluginEvent(@NotNull ExtendedPlugin plugin) {
        this.plugin = plugin;
    }

    @NotNull
    public ExtendedPlugin getPlugin() {
        return plugin;
    }
}
