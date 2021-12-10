package eu.software4you.ulib;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.impl.velocity.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.impl.velocity.usercache.MainUserCacheImpl;
import eu.software4you.velocity.plugin.VelocityJavaPlugin;
import lombok.SneakyThrows;
import org.slf4j.Logger;

import java.nio.file.Path;

@Plugin(
        id = "ulib",
        name = "uLib",
        authors = "Software4You.eu",
        url = "https://software4you.eu",
        version = "{{version}}"
)
public class ULibVelocityPlugin extends VelocityJavaPlugin {

    static {
        Properties.getInstance().MODE = RunMode.VELOCITY;
        ULib.init();
    }

    private ProxyServerBridgeImpl proxyServerBridge;
    private SqlEngine mainUserCacheEngine;

    @Inject
    public ULibVelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataPath) {
        super("ulib", proxyServer, logger, dataPath.toFile());
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
        proxyServerBridge = ProxyServerBridgeImpl.init(this);
        registerEvents(proxyServerBridge);
        getProxyServer().getChannelRegistrar().register(ProxyServerBridgeImpl.IDENTIFIER);

        MainUserCacheImpl.init(this, mainUserCacheEngine = new SqlEngine());
    }


    @SneakyThrows
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
        if (mainUserCacheEngine.isConnected()) {
            mainUserCacheEngine.disconnect();
        }
        if (proxyServerBridge != null) {
            getProxyServer().getChannelRegistrar().unregister(ProxyServerBridgeImpl.IDENTIFIER);
            unregisterEvents(proxyServerBridge);
        }
    }
}
