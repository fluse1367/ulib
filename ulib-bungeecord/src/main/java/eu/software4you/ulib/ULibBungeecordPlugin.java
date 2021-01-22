package eu.software4you.ulib;

import eu.software4you.bungeecord.plugin.ExtendedProxyPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeInit;
import eu.software4you.ulib.minecraft.usercache.UserCacheInit;
import lombok.SneakyThrows;

public class ULibBungeecordPlugin extends ExtendedProxyPlugin {
    static {
        ULib.makeReady();
    }

    private ProxyServerBridgeImpl proxyServerBridgeImpl;
    private SqlEngine mainUserCacheEngine;

    @Override
    public void onEnable() {
        try {
            proxyServerBridgeImpl = new ProxyServerBridgeImpl(this);
            ProxyServerBridgeInit.proxyServerBridge(proxyServerBridgeImpl);
            registerEvents(proxyServerBridgeImpl);
            getProxy().registerChannel(proxyServerBridgeImpl.CHANNEL);

            UserCacheInit.constructor(UserCacheImpl::new);
            UserCacheInit.pluginBase(this);
            UserCacheInit.engine(mainUserCacheEngine = new SqlEngine());
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
