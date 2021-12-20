package eu.software4you.ulib.minecraft.api.plugin.controllers;

/**
 * Base for a task scheduler.
 *
 * @see ASyncSchedulerController
 * @see SyncSchedulerController
 */
public interface SchedulerControllerBase {
    /**
     * Attempts to cancel all future tasks.
     */
    void cancelAllTasks();
}
