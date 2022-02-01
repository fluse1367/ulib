package eu.software4you.bungeecord.plugin;

import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.plugin.controllers.SchedulerController;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

/**
 * Extended version of BungeeCord's {@link Plugin}.
 *
 * @see ExtendedProxyPlugin
 */
@SuppressWarnings("NullableProblems")
public abstract class ExtendedPlugin extends Plugin implements PluginBase<Listener, ScheduledTask, CommandSender>,
        SchedulerController<ScheduledTask>, ProxyCommandController {
    @Override
    public @NotNull String getName() {
        return getDescription().getName();
    }

    public abstract void saveResource(String resourcePath, boolean replace);
}
