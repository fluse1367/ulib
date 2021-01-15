package eu.software4you.ulib.minecraft.plugin.controllers;

public interface EventController<L> {
    void registerEvents(L listener);

    void unregisterEvents(L listener);

    void unregisterAllEvents();
}
