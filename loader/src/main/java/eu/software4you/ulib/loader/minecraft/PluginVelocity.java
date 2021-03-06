package eu.software4you.ulib.loader.minecraft;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.init.InitAccess;
import eu.software4you.ulib.loader.install.Installer;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.nio.file.Path;

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
        Installer.installMe();
    }

    private final ProxyServer proxyServer;
    private final Logger logger;
    private final Path dataDir;
    @SuppressWarnings("FieldCanBeLocal") // prevent the substitute to be gc'd
    private Object pluginSubstitute;

    @Inject
    public PluginVelocity(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataDir) {
        this.proxyServer = proxyServer;
        this.logger = logger;
        this.dataDir = dataDir;
    }

    @SneakyThrows
    @Subscribe
    public void onInit(ProxyInitializeEvent e) {
        this.pluginSubstitute = InitAccess.getInstance().construct("velocity", "impl.PluginSubst",
                this, proxyServer, logger, dataDir);
    }
}
