package eu.software4you.ulib.minecraft.plugin.controllers;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

/**
 * Asynchronous task scheduler controller.
 * All tasks are run asynchronously to the main thread.
 *
 * @param <R> task object return type
 */
public interface ASyncSchedulerController<R> extends SchedulerControllerBase {
    /**
     * Runs a asynchronous task immediately.
     *
     * @param runnable the task to run
     * @return the task object
     */
    @NotNull
    R async(@NotNull Runnable runnable);

    /**
     * Runs a asynchronous task after a certain delay.
     *
     * @param runnable the task to run
     * @param delay    the delay
     * @param unit     the delay's unit
     * @return the task object
     */
    @NotNull
    R async(@NotNull Runnable runnable, long delay, @NotNull TimeUnit unit);

    /**
     * Runs a asynchronous task after a certain delay.
     *
     * @param runnable the task to run
     * @param delay    the delay in milliseconds
     * @return the task object
     */
    @NotNull
    default R async(@NotNull Runnable runnable, long delay) {
        return async(runnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * Runs a asynchronous task after a certain delay and repeats it periodically.
     *
     * @param runnable the task to run
     * @param delay    the delay
     * @param period   the delay between each period
     * @param unit     the delay/period unit
     * @return the task object
     */
    @NotNull
    R async(@NotNull Runnable runnable, long delay, long period, @NotNull TimeUnit unit);

    /**
     * Runs a asynchronous task after a certain delay and repeats it periodically.
     *
     * @param runnable the task to run
     * @param delay    the delay in milliseconds
     * @param period   the delay in milliseconds between each period
     * @return the task object
     */
    @NotNull
    default R async(@NotNull Runnable runnable, long delay, long period) {
        return async(runnable, delay, period, TimeUnit.MILLISECONDS);
    }
}
