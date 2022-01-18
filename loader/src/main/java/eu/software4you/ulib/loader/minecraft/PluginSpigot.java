package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class PluginSpigot extends JavaPlugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.SPIGOT);
    }

    @Override
    public void onLoad() {
        Init.init(getClass());
    }

    @Override
    public void onEnable() {
        Optional.ofNullable(getCommand("ulib"))
                .orElseThrow()
                .setExecutor((sender, command, label, args) -> {
                    sender.sendMessage("%suLib version %s".formatted(ChatColor.GREEN, ULib.get().getVersion()));
                    return true;
                });
    }
}
