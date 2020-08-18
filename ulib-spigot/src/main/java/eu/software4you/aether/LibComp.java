package eu.software4you.aether;

import eu.software4you.reflection.ReflectUtil;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;

import java.io.File;

class LibComp implements Comp {

    @SneakyThrows
    @Override
    public void check(String coords, Class<?> clazz) {
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?> pluginClassLoader = Class.forName("org.bukkit.plugin.java.PluginClassLoader");
        if (!pluginClassLoader.isInstance(classLoader)) {
            // the class to check is not a class from a spigot plugin
            ULib.getInstance().getLogger().fine(String.format("%s (%s) is using the ClassLoader %s (%s)",
                    coords, clazz.getName(), classLoader.getClass().getName(),
                    new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()).getName()));
            return;
        }
        try {
            File file = new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI());

            Plugin plugin = (Plugin) ReflectUtil.forceCall(pluginClassLoader, classLoader, "plugin");

            ULib.getInstance().getLogger().warning(String.format("Library %s was found in the plugin %s (%s). This may cause fatal compatibility issues." +
                    " If those arise consider removing the library from %2$s (%3$s) as it will be loaded by uLib anyways.", coords, plugin.getName(), file));

        } catch (Throwable ignored) {
        }
    }
}