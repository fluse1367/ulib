package eu.software4you.ulib.minecraft.plugin.controllers;

import java.util.concurrent.TimeUnit;

public interface SyncSchedulerController<R> extends SchedulerControllerBase {
    R sync(Runnable runnable);

    R sync(Runnable runnable, long delay, TimeUnit unit);

    default R sync(Runnable runnable, long delay) {
        return sync(runnable, delay, TimeUnit.MILLISECONDS);
    }

    R sync(Runnable runnable, long delay, long period, TimeUnit unit);

    default R sync(Runnable runnable, long delay, long period) {
        return sync(runnable, delay, period, TimeUnit.MILLISECONDS);
    }
}
