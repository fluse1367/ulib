import eu.software4you.ulib.minecraft.api.internal.Providers;
import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.minecraft.api.usercache.MainUserCache;
import eu.software4you.ulib.velocity.api.internal.Providers.ProviderLayout;
import eu.software4you.ulib.velocity.impl.plugin.LayoutProvider;
import eu.software4you.ulib.velocity.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.velocity.impl.usercache.MainUserCacheImpl;
import eu.software4you.ulib.velocity.impl.usercache.UserCacheProvider;

module ulib.velocity {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core;
    requires ulib.core.api;
    requires ulib.velocity.api;
    requires ulib.minecraft;
    requires ulib.minecraft.api;

    requires static com.velocitypowered.api;
    requires com.google.gson;

    provides MainUserCache with MainUserCacheImpl;
    provides ProviderLayout with LayoutProvider;
    provides Providers.ProviderUserCache with UserCacheProvider;
    provides ProxyServerBridge with ProxyServerBridgeImpl;

    opens eu.software4you.ulib.velocity.impl.proxybridge;
    opens eu.software4you.ulib.velocity.impl;
}