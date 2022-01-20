import eu.software4you.ulib.bungeecord.api.internal.Providers.ProviderLayout;
import eu.software4you.ulib.bungeecord.impl.plugin.LayoutImpl;
import eu.software4you.ulib.bungeecord.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.bungeecord.impl.usercache.MainUserCacheImpl;
import eu.software4you.ulib.bungeecord.impl.usercache.UserCacheImpl;
import eu.software4you.ulib.minecraft.api.internal.Providers;
import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.minecraft.api.usercache.MainUserCache;

module ulib.bungeecord {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core;
    requires ulib.core.api;
    requires ulib.minecraft;
    requires ulib.minecraft.api;
    requires ulib.bungeecord.api;

    requires static bungeecord.api;
    requires static bungeecord.event;
    requires static com.google.gson;
    requires org.yaml.snakeyaml;


    provides MainUserCache with MainUserCacheImpl;
    provides ProviderLayout with LayoutImpl.LayoutProvider;
    provides Providers.ProviderUserCache with UserCacheImpl.UserCacheProvider;
    provides ProxyServerBridge with ProxyServerBridgeImpl;

}