package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.install.Installer;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.fabricmc.loader.impl.entrypoint.EntrypointUtils;

public class ModFabric implements PreLaunchEntrypoint {

    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.FABRIC);
        Installer.installTo(ClassLoader.getSystemClassLoader());
        Installer.installMe();
    }

    @Override
    public void onPreLaunch() {
        EntrypointUtils.invoke("ulibPreLaunch", PreLaunchEntrypoint.class, PreLaunchEntrypoint::onPreLaunch);
    }
}
