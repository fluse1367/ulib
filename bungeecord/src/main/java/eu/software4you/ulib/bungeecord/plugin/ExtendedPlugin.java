package eu.software4you.ulib.bungeecord.plugin;

import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.plugin.controllers.SchedulerController;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.*;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import org.jetbrains.annotations.NotNull;

/**
 * Extended version of BungeeCord's {@link Plugin}.
 *
 * @see ExtendedProxyPlugin
 */
@SuppressWarnings("NullableProblems")
public abstract class ExtendedPlugin extends Plugin implements PluginBase<Listener, ScheduledTask>,
        SchedulerController<ScheduledTask>, ProxyCommandController {

    public ExtendedPlugin() {
        super();
    }

    public ExtendedPlugin(@NotNull ProxyServer proxy, @NotNull PluginDescription description) {
        super(proxy, description);
    }

    @Override
    public @NotNull String getName() {
        return getDescription().getName();
    }

    public abstract void saveResource(@NotNull String resourcePath, boolean replace);
}
