package eu.software4you.ulib.bungeecord.impl;

import eu.software4you.ulib.bungeecord.api.plugin.ExtendedProxyPlugin;
import eu.software4you.ulib.bungeecord.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.bungeecord.impl.usercache.MainUserCacheImpl;
import eu.software4you.ulib.core.api.configuration.ConversionPolicy;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import lombok.SneakyThrows;

public class PluginSubst extends ExtendedProxyPlugin {
    private ProxyServerBridgeImpl proxyServerBridgeImpl;
    private SqlEngine mainUserCacheEngine;

    @Override
    public void onEnable() {
        try {
            getConf().setConversionPolicy(ConversionPolicy.THROW_EXCEPTION);

            ProxyServerBridgeImpl.init(this);
            proxyServerBridgeImpl = (ProxyServerBridgeImpl) ProxyServerBridge.getInstance();
            registerEvents(proxyServerBridgeImpl);
            getProxy().registerChannel(ProxyServerBridge.CHANNEL);

            MainUserCacheImpl.init(this, mainUserCacheEngine = new SqlEngine());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SneakyThrows
    @Override
    public void onDisable() {
        if (mainUserCacheEngine.isConnected()) {
            mainUserCacheEngine.disconnect();
        }
        if (proxyServerBridgeImpl != null) {
            getProxy().unregisterChannel(proxyServerBridgeImpl.CHANNEL);
            unregisterEvents(proxyServerBridgeImpl);
        }
    }
}
