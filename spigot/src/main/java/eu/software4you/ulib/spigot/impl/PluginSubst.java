package eu.software4you.ulib.spigot.impl;

import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.LazyValue;
import eu.software4you.ulib.minecraft.impl.proxybridge.AbstractProxyServerBridge;
import eu.software4you.ulib.minecraft.impl.usercache.AbstractUserCache;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.spigot.impl.configuration.BukkitSerializationAdapter;
import eu.software4you.ulib.spigot.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.spigot.impl.usercache.UserCacheImpl;
import eu.software4you.ulib.spigot.plugin.ExtendedJavaPlugin;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.messaging.Messenger;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class PluginSubst extends ExtendedJavaPlugin implements Listener {
    private static final LazyValue<String> PLAIN_MC_VERSION;
    private static final String PROP_KEY = "ulib.plugin_status";
    private static PluginSubst instance = null;

    static {
        // check for already existing instance
        if (System.getProperties().containsKey(PROP_KEY)) {
            System.err.println("[uLib] A previous uLib instance was detected. uLib does not support being reloaded." +
                               " Please fully stop the server instead of doing a reload, or reload specific plugins with a plugin-manager.");
            throw new IllegalStateException("Reloading not supported");
        }

        var server = Bukkit.getServer();
        PLAIN_MC_VERSION = new LazyValue<>(() -> ReflectUtil.<String>call(server.getClass(), server, "getServer().getVersion()")
                .orElseThrow());

        SerializationAdapters.getInstance().registerAdapter(ConfigurationSerializable.class, new BukkitSerializationAdapter());

        System.setProperty(PROP_KEY, "cinit");
    }

    private final Plugin plugin;

    public PluginSubst(Plugin plugin, JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
        this.plugin = plugin;
    }

    public static PluginSubst getInstance() {
        return instance;
    }

    public static String getPlainMcVersion() {
        return PLAIN_MC_VERSION.get();
    }

    @Override
    public void onEnable() {
        try {
            registerEvents(instance = this);

            ProxyServerBridgeImpl proxyServerBridgeImpl = new ProxyServerBridgeImpl(this);
            AbstractProxyServerBridge.INSTANCE.setInstance(proxyServerBridgeImpl);

            Messenger messenger = Bukkit.getMessenger();
            messenger.registerOutgoingPluginChannel(this, ProxyServerBridge.CHANNEL);
            messenger.registerIncomingPluginChannel(this, ProxyServerBridge.CHANNEL, proxyServerBridgeImpl);
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
            messenger.registerIncomingPluginChannel(this, "BungeeCord", proxyServerBridgeImpl);


            AbstractUserCache.PLUGIN_INSTANCE.setInstance(this);
            AbstractUserCache.PROVIDER.setInstance(UserCacheImpl::new);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        System.setProperty(PROP_KEY, "enabled");
    }

    @SneakyThrows
    @Override
    public void onDisable() {
        var db = UserCacheImpl.MAIN_CACHE_DB.getUnsafe();
        if (db != null && db.isConnected()) {
            db.disconnect();
        }

        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);

        System.setProperty(PROP_KEY, "disabled");
    }

    @Override
    public @NotNull Plugin getPluginObject() {
        return plugin;
    }
}
