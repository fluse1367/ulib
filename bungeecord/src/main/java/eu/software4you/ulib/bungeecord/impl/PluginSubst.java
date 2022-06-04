package eu.software4you.ulib.bungeecord.impl;

import eu.software4you.ulib.bungeecord.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.bungeecord.impl.usercache.UserCacheImpl;
import eu.software4you.ulib.bungeecord.plugin.ExtendedProxyPlugin;
import eu.software4you.ulib.minecraft.impl.SharedConstants;
import eu.software4you.ulib.minecraft.impl.proxybridge.AbstractProxyServerBridge;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;
import org.jetbrains.annotations.NotNull;

public class PluginSubst extends ExtendedProxyPlugin {
    private final Plugin plugin;
    private ProxyServerBridgeImpl proxyServerBridgeImpl;

    public PluginSubst(Plugin plugin, ProxyServer proxy, PluginDescription description) {
        super(proxy, description);
        this.plugin = plugin;
        SharedConstants.BASE.setInstance(this);
    }

    @Override
    public void onEnable() {
        try {
            AbstractProxyServerBridge.INSTANCE.setInstance(this.proxyServerBridgeImpl = new ProxyServerBridgeImpl(this));

            registerEvents(proxyServerBridgeImpl);
            getProxy().registerChannel(AbstractProxyServerBridge.CHANNEL);

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

    @Override
    public @NotNull Object getPluginObject() {
        return plugin;
    }
}
