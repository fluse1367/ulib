package eu.software4you.ulib.minecraft.plugin.controllers;

/**
 * A scheduler controller that combines asynchronous and synchronous task scheduling.
 *
 * @param <R> task object return type
 */
public interface SchedulerController<R> extends ASyncSchedulerController<R>, SyncSchedulerController<R> {

}
