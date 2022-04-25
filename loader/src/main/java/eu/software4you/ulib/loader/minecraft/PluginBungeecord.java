package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.init.InitAccess;
import eu.software4you.ulib.loader.install.Installer;
import net.md_5.bungee.api.plugin.Plugin;

public class PluginBungeecord extends Plugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.BUNGEECORD);
        Installer.installMe();
    }

    private Plugin pluginSubstitute;

    @Override
    public void onLoad() {
        this.pluginSubstitute = (Plugin) InitAccess.getInstance().construct("bungeecord", "eu.software4you.ulib.bungeecord.impl.PluginSubst",
                this, getProxy(), getDescription());
    }

    @Override
    public void onEnable() {
        pluginSubstitute.onEnable();
    }

    @Override
    public void onDisable() {
        pluginSubstitute.onDisable();
    }
}
