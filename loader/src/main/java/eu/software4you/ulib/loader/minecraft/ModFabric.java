package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.install.Installer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class ModFabric implements PreLaunchEntrypoint {

    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.STANDALONE);
        Installer.installTo(ClassLoader.getSystemClassLoader());
    }

    @Override
    public void onPreLaunch() {

    }
}
