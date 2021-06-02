package eu.software4you.velocity.plugin;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.scheduler.ScheduledTask;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

public interface VelocityPlugin extends PluginBase<Object, ScheduledTask, Audience> {

    @Override
    @NotNull
    Layout getLayout();

    String getId();

    ProxyServer getProxyServer();

    Logger getLogger();

    File getDataFolder();

    File getFile();

    void saveResource(String resourcePath, boolean replace);

    @Deprecated
    @Override
    void cancelAllTasks();

    @Override
    @Deprecated
    default ScheduledTask sync(Runnable runnable) {
        throw new UnsupportedOperationException("Velocity does not provide synchronous tasks.");
    }

    @Override
    @Deprecated
    default ScheduledTask sync(Runnable runnable, long delay, TimeUnit unit) {
        throw new UnsupportedOperationException("Velocity does not provide synchronous tasks.");
    }

    @Override
    @Deprecated
    default ScheduledTask sync(Runnable runnable, long delay, long period, TimeUnit unit) {
        throw new UnsupportedOperationException("Velocity does not provide synchronous tasks.");
    }
}
