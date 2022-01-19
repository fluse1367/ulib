package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.loader.install.Installer;
import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class PluginSpigot extends JavaPlugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.SPIGOT);
    }

    private Plugin pluginSubstitute;

    @Override
    public void onLoad() {
        Init.init(getClass());
    }

    @SneakyThrows
    @Override
    public void onEnable() {
        var loader = Installer.getModule().getLayer().findLoader("ulib.spigot");
        var cl = Class.forName("eu.software4you.ulib.spigot.impl.PluginSubst", true, loader);
        this.pluginSubstitute = (Plugin) cl.getConstructors()[0].newInstance(this);

        pluginSubstitute.onEnable();

        Optional.ofNullable(getCommand("ulib"))
                .orElseThrow()
                .setExecutor((sender, command, label, args) -> {
                    sender.sendMessage("%suLib version %s".formatted(ChatColor.GREEN, ULib.get().getVersion()));
                    return true;
                });
    }

    @Override
    public void onDisable() {
        pluginSubstitute.onDisable();
    }
}
