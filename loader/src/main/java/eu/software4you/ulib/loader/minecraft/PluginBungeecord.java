package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.install.Installer;
import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import net.md_5.bungee.api.plugin.Plugin;

public class PluginBungeecord extends Plugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.BUNGEECORD);
        Installer.installMe();
    }
}
