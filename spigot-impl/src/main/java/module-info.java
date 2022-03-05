import eu.software4you.ulib.minecraft.api.internal.Providers.ProviderUserCache;
import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.minecraft.api.usercache.MainUserCache;
import eu.software4you.ulib.spigot.api.internal.Providers;
import eu.software4you.ulib.spigot.api.inventorymenu.builder.EntryFactory;
import eu.software4you.ulib.spigot.api.inventorymenu.builder.MenuFactory;
import eu.software4you.ulib.spigot.impl.inventorymenu.MenuManagerImpl;
import eu.software4you.ulib.spigot.impl.inventorymenu.factory.EntryFactoryImpl;
import eu.software4you.ulib.spigot.impl.inventorymenu.factory.MenuFactoryImpl;
import eu.software4you.ulib.spigot.impl.plugin.LayoutImpl;
import eu.software4you.ulib.spigot.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.spigot.impl.usercache.MainUserCacheImpl;
import eu.software4you.ulib.spigot.impl.usercache.UserCacheImpl;

module ulib.spigot {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core;
    requires ulib.core.api;
    requires ulib.spigot.api;
    requires ulib.minecraft;
    requires ulib.minecraft.api;

    requires org.yaml.snakeyaml;
    requires java.logging;
    requires static org.bukkit;
    requires static com.google.gson;
    requires static com.google.common;

    // via extra-java-module-info
    requires xseries;

    provides EntryFactory with EntryFactoryImpl;
    provides MainUserCache with MainUserCacheImpl;
    provides MenuFactory with MenuFactoryImpl;
    provides ProviderUserCache with UserCacheImpl.UserCacheProvider;
    provides Providers.ProviderLayout with LayoutImpl.LayoutProvider;
    provides Providers.ProviderMenuManager with MenuManagerImpl.MenuManagerProvider;
    provides ProxyServerBridge with ProxyServerBridgeImpl;


    opens eu.software4you.ulib.spigot.impl;
    opens eu.software4you.ulib.spigot.impl.proxybridge;
}