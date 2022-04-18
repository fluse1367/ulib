package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.install.Installer;
import net.fabricmc.api.ModInitializer;

public class ModFabric implements ModInitializer {

    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.STANDALONE);
        Installer.installTo(ClassLoader.getSystemClassLoader());
    }

    @Override
    public void onInitialize() {

    }
}
