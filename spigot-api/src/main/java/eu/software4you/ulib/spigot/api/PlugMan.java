package eu.software4you.ulib.spigot.api;

import eu.software4you.ulib.core.api.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.event.Event;
import org.bukkit.plugin.*;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class PlugMan {
    public static boolean unload(final Plugin plugin) {
        final String name = plugin.getName();
        final PluginManager pluginManager = Bukkit.getPluginManager();
        SimpleCommandMap commandMap = null;
        List<Plugin> plugins = null;
        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;
        boolean reloadlisteners = true;
        if (pluginManager != null) {
            Bukkit.getScheduler().cancelTasks(plugin);
            pluginManager.disablePlugin(plugin);
            try {
                final Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);
                final Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);
                try {
                    final Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e3) {
                    reloadlisteners = false;
                }
                final Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);
                final Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalAccessException e2) {
                e2.printStackTrace();
                return false;
            }
        }
        pluginManager.disablePlugin(plugin);
        if (plugins != null) {
            plugins.remove(plugin);
        }
        if (names != null) {
            names.remove(name);
        }
        if (listeners != null && reloadlisteners) {
            for (final SortedSet<RegisteredListener> set : listeners.values()) {
                set.removeIf(value -> value.getPlugin() == plugin);
            }
        }
        if (commandMap != null) {
            final Iterator<Map.Entry<String, Command>> it2 = commands.entrySet().iterator();
            while (it2.hasNext()) {
                final Map.Entry<String, Command> entry = it2.next();
                if (entry.getValue() instanceof final PluginCommand c) {
                    if (c.getPlugin() != plugin) {
                        continue;
                    }
                    c.unregister(commandMap);
                    it2.remove();
                }
            }
        }
        final ClassLoader cl = plugin.getClass().getClassLoader();
        if (cl instanceof URLClassLoader) {
            try {
                final Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);
                final Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex4) {
                ex4.printStackTrace();
            }
            try {
                ((URLClassLoader) cl).close();
            } catch (IOException ex2) {
                ex2.printStackTrace();
            }
        }
        System.gc();
        return true;
    }

    public static boolean load(File pluginFile) {
        Plugin target = null;
        final File pluginDir = new File("plugins");
        try {
            target = Bukkit.getPluginManager().loadPlugin(pluginFile);
        } catch (InvalidDescriptionException | InvalidPluginException e) {
            e.printStackTrace();
            return false;
        }
        target.onLoad();
        Bukkit.getPluginManager().enablePlugin(target);
        return true;
    }

    public static boolean reload(Plugin plugin) {
        return unload(plugin) && load(FileUtil.getClassFile(plugin.getClass()).orElse(null));
    }
}
