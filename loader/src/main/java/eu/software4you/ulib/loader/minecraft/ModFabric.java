package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.install.Installer;
import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import net.fabricmc.api.ModInitializer;

public class ModFabric implements ModInitializer {

    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.STANDALONE);
        Installer.installMe();
    }

    @Override
    public void onInitialize() {

    }
}
