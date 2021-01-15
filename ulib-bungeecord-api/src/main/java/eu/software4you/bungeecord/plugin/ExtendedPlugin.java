package eu.software4you.bungeecord.plugin;

import eu.software4you.ulib.minecraft.plugin.PluginBase;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public abstract class ExtendedPlugin extends Plugin implements PluginBase<Listener, ScheduledTask>, ProxyCommandController {
    @Override
    public String getName() {
        return getDescription().getName();
    }

    public abstract void saveResource(String resourcePath, boolean replace);
}
