package eu.software4you.ulib.bungeecord.impl;

import eu.software4you.ulib.bungeecord.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.bungeecord.impl.usercache.UserCacheImpl;
import eu.software4you.ulib.bungeecord.plugin.ExtendedProxyPlugin;
import eu.software4you.ulib.minecraft.impl.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import lombok.SneakyThrows;

public class PluginSubst extends ExtendedProxyPlugin {
    private ProxyServerBridgeImpl proxyServerBridgeImpl;

    @Override
    public void onEnable() {
        try {
            ProxyServerBridge.INSTANCE.setInstance(this.proxyServerBridgeImpl = new ProxyServerBridgeImpl(this));

            registerEvents(proxyServerBridgeImpl);
            getProxy().registerChannel(ProxyServerBridge.CHANNEL);

            AbstractUserCache.PLUGIN_INSTANCE.setInstance(this);
            AbstractUserCache.PROVIDER.setInstance(UserCacheImpl::new);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @SneakyThrows
    @Override
    public void onDisable() {
        var db = AbstractUserCache.MAIN_CACHE_DB.getUnsafe();
        if (db != null && db.isConnected()) {
            db.disconnect();
        }
        if (proxyServerBridgeImpl != null) {
            getProxy().unregisterChannel(eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridge.CHANNEL);
            unregisterEvents(proxyServerBridgeImpl);
        }
    }
}
