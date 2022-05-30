package eu.software4you.ulib.minecraft.plugin.controllers;

import org.jetbrains.annotations.NotNull;

/**
 * Controller for event listener management.
 *
 * @param <L> the listener type
 */
public interface EventController<L> {
    /**
     * Registers a event listener.
     *
     * @param listener the listener to register
     */
    void registerEvents(@NotNull L listener);

    /**
     * Removes a listener's registration.
     *
     * @param listener the listener whose registration to remove
     */
    void unregisterEvents(@NotNull L listener);

    /**
     * Attempts to remove all registrations from every registered event listener.
     */
    void unregisterAllEvents();
}
