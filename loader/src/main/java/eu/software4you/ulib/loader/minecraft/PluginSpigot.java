package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginSpigot extends JavaPlugin {
    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.SPIGOT);
    }

    @Override
    public void onLoad() {
        Init.init(getClass());
    }
}
