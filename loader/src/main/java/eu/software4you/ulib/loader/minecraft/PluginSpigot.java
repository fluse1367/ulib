package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.init.InitAccess;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Optional;

public class PluginSpigot extends JavaPlugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.SPIGOT);
        Installer.installMe();
    }

    private Plugin pluginSubstitute;

    @SneakyThrows
    @Override
    public void onLoad() {
        this.pluginSubstitute = (Plugin) InitAccess.getInstance().construct("spigot", "eu.software4you.ulib.spigot.impl.PluginSubst",
                this, getPluginLoader(), getDescription(), getDataFolder(), getFile());
    }

    @Override
    public void onEnable() {
        pluginSubstitute.onEnable();

        Optional.ofNullable(getCommand("ulib"))
                .orElseThrow()
                .setExecutor((sender, command, label, args) -> {
                    sender.sendMessage("%suLib version %s".formatted(ChatColor.GREEN, getDescription().getVersion()));
                    return true;
                });
    }

    @Override
    public void onDisable() {
        pluginSubstitute.onDisable();
    }
}
