package eu.software4you.ulib.loader.minecraft;

import com.velocitypowered.api.plugin.Plugin;
import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;

@Plugin(
        id = "ulib3",
        name = "uLib 3 loader",
        authors = "fluse1367",
        url = "https://software4you.eu",
        version = "{{project.version}}"
)
public class PluginVelocity {

    static {
        EnvironmentProvider.initAs(EnvironmentProvider.Environment.VELOCITY);
    }

    public PluginVelocity() {
        Init.init(getClass());
    }
}
