package eu.software4you.minecraft.plugin;

import org.bukkit.event.Listener;

public interface BukkitEventController {
    void registerEvents(Listener listener);

    void unregisterEvents(Listener listener);

    void unregisterAllEvents();
}
