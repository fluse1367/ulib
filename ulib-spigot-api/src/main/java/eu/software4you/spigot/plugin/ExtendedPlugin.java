package eu.software4you.spigot.plugin;

import eu.software4you.ulib.minecraft.plugin.PluginBase;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public interface ExtendedPlugin extends Plugin, PluginBase<Listener, BukkitTask, CommandSender> {
}
