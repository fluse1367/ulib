package eu.software4you.proxy.plugin;


import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.concurrent.TimeUnit;

public interface ProxySchedulerController {

    ScheduledTask async(Runnable runnable);

    ScheduledTask async(Runnable runnable, long delay, TimeUnit unit);

    default ScheduledTask async(Runnable runnable, long delay) {
        return async(runnable, delay, TimeUnit.MILLISECONDS);
    }

    ScheduledTask async(Runnable runnable, long delay, long period, TimeUnit unit);

    default ScheduledTask async(Runnable runnable, long delay, long period) {
        return async(runnable, delay, period, TimeUnit.MILLISECONDS);
    }

    void cancelAllTasks();
}
