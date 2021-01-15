package eu.software4you.ulib.minecraft.plugin.controllers;

import java.util.concurrent.TimeUnit;

public interface ASyncSchedulerController<R> extends SchedulerControllerBase {
    R async(Runnable runnable);

    R async(Runnable runnable, long delay, TimeUnit unit);

    default R async(Runnable runnable, long delay) {
        return async(runnable, delay, TimeUnit.MILLISECONDS);
    }

    R async(Runnable runnable, long delay, long period, TimeUnit unit);

    default R async(Runnable runnable, long delay, long period) {
        return async(runnable, delay, period, TimeUnit.MILLISECONDS);
    }
}
