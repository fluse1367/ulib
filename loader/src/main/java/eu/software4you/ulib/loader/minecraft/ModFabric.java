package eu.software4you.ulib.loader.minecraft;

import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import net.fabricmc.api.ModInitializer;

public class ModFabric implements ModInitializer {

    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.STANDALONE);
    }

    @Override
    public void onInitialize() {
        Init.init(getClass());
    }
}
