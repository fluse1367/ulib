package eu.software4you.ulib.velocity.impl;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.software4you.ulib.minecraft.impl.SharedConstants;
import eu.software4you.ulib.minecraft.impl.proxybridge.AbstractProxyServerBridge;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import eu.software4you.ulib.velocity.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.velocity.impl.usercache.UserCacheImpl;
import eu.software4you.ulib.velocity.plugin.VelocityJavaPlugin;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.nio.file.Path;

public class PluginSubst extends VelocityJavaPlugin {

    private final Object plugin;
    private final ProxyServerBridgeImpl proxyServerBridge;

    public PluginSubst(Object plugin, ProxyServer proxyServer, Logger logger, Path dataDir) {
        super("ulib", proxyServer, logger, dataDir);
        this.plugin = plugin;

        registerEvents(this);

        AbstractProxyServerBridge.INSTANCE.setInstance(proxyServerBridge = new ProxyServerBridgeImpl(this));

        registerEvents(proxyServerBridge);
        getProxyServer().getChannelRegistrar().register(ProxyServerBridgeImpl.IDENTIFIER);

        SharedConstants.BASE.setInstance(this);

        AbstractUserCache.PROVIDER.setInstance(UserCacheImpl::new);
    }

    @Override
    public @NotNull Object getPluginObject() {
        return plugin;
    }

    @SneakyThrows
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent e) {
        var db = AbstractUserCache.MAIN_CACHE_DB.getUnsafe();
        if (db != null && db.isConnected()) {
            db.disconnect();
        }
        if (proxyServerBridge != null) {
            getProxyServer().getChannelRegistrar().unregister(ProxyServerBridgeImpl.IDENTIFIER);
            unregisterEvents(proxyServerBridge);
        }
    }
}
