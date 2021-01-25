package eu.software4you.ulib;

import eu.software4you.bungeecord.plugin.ExtendedProxyPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.impl.bungeecord.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.impl.bungeecord.usercache.MainUserCacheImpl;
import lombok.SneakyThrows;

public class ULibBungeecordPlugin extends ExtendedProxyPlugin {
    static {
        ULib.init();
    }

    private ProxyServerBridgeImpl proxyServerBridgeImpl;
    private SqlEngine mainUserCacheEngine;

    @Override
    public void onEnable() {
        try {
            proxyServerBridgeImpl = ProxyServerBridgeImpl.init(this);
            registerEvents(proxyServerBridgeImpl);
            getProxy().registerChannel(proxyServerBridgeImpl.CHANNEL);

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
