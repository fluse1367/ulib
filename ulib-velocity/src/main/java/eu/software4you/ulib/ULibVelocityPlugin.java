package eu.software4you.ulib;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeInit;
import eu.software4you.ulib.minecraft.usercache.UserCacheInit;
import eu.software4you.velocity.plugin.VelocityJavaPlugin;

import java.nio.file.Path;
import java.util.logging.Logger;

@Plugin(
        id = "ulib",
        name = "uLib",
        authors = "Software4You.eu",
        url = "https://software4you.eu",
        version = "{{version}}"
)
public class ULibVelocityPlugin extends VelocityJavaPlugin {

    private ProxyServerBridgeImpl proxyServerBridge;

    @Inject
    public ULibVelocityPlugin(ProxyServer proxyServer, Logger logger, @DataDirectory Path dataPath) {
        super("ulib", proxyServer, logger, dataPath.toFile());
    }

    @Subscribe
    public void onProxyInit(ProxyInitializeEvent e) {
        proxyServerBridge = new ProxyServerBridgeImpl(this);
        ProxyServerBridgeInit.proxyServerBridge(proxyServerBridge);
        registerEvents(proxyServerBridge);
        getProxyServer().getChannelRegistrar().register(ProxyServerBridgeImpl.IDENTIFIER);

        UserCacheInit.userCache(UserCacheImpl.class);
        UserCacheInit.pluginBase(this);
    }


    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
        if (proxyServerBridge != null) {
            getProxyServer().getChannelRegistrar().unregister(ProxyServerBridgeImpl.IDENTIFIER);
            unregisterEvents(proxyServerBridge);
        }
    }
}
