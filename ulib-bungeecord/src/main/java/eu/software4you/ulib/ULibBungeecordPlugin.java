package eu.software4you.ulib;

import eu.software4you.bungeecord.plugin.ExtendedProxyPlugin;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeInit;
import eu.software4you.ulib.minecraft.usercache.UserCacheInit;

public class ULibBungeecordPlugin extends ExtendedProxyPlugin {
    static {
        ULib.makeReady();
    }

    private ProxyServerBridgeImpl proxyServerBridgeImpl;

    @Override
    public void onEnable() {
        try {
            proxyServerBridgeImpl = new ProxyServerBridgeImpl(this);
            ProxyServerBridgeInit.proxyServerBridge(proxyServerBridgeImpl);
            registerEvents(proxyServerBridgeImpl);
            getProxy().registerChannel(proxyServerBridgeImpl.CHANNEL);

            UserCacheInit.userCache(UserCacheImpl.class);
            UserCacheInit.pluginBase(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        if (proxyServerBridgeImpl != null) {
            getProxy().unregisterChannel(proxyServerBridgeImpl.CHANNEL);
            unregisterEvents(proxyServerBridgeImpl);
        }
    }
}
