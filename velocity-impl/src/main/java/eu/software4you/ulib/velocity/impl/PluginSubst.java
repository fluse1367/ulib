package eu.software4you.ulib.velocity.impl;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.velocity.api.plugin.VelocityJavaPlugin;
import eu.software4you.ulib.velocity.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.velocity.impl.usercache.MainUserCacheImpl;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;

public class PluginSubst extends VelocityJavaPlugin {

    private final Object plugin;
    private final ProxyServerBridgeImpl proxyServerBridge;
    private final SqlEngine mainUserCacheEngine;

    public PluginSubst(Object plugin, ProxyServer proxyServer, Logger logger, File dataFolder) {
        super("ulib", proxyServer, logger, dataFolder);
        this.plugin = plugin;

        registerEvents(this);

        ProxyServerBridgeImpl.init(this);
        proxyServerBridge = (ProxyServerBridgeImpl) ProxyServerBridge.getInstance();

        registerEvents(proxyServerBridge);
        getProxyServer().getChannelRegistrar().register(ProxyServerBridgeImpl.IDENTIFIER);

        MainUserCacheImpl.init(this, mainUserCacheEngine = new SqlEngine());
    }

    @Override
    public @NotNull Object getPluginObject() {
        return plugin;
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
