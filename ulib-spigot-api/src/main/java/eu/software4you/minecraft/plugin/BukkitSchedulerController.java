package eu.software4you.minecraft.plugin;

import org.bukkit.scheduler.BukkitTask;

public interface BukkitSchedulerController {
    BukkitTask sync(Runnable runnable);

    BukkitTask sync(Runnable runnable, long delay);

    BukkitTask sync(Runnable runnable, long delay, long period);

    BukkitTask async(Runnable runnable);

    BukkitTask async(Runnable runnable, long delay);

    BukkitTask async(Runnable runnable, long delay, long period);

    void cancelAllTasks();
}
