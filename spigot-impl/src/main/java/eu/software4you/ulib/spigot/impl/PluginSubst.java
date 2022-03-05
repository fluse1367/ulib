package eu.software4you.ulib.spigot.impl;

import eu.software4you.ulib.core.api.configuration.ConversionPolicy;
import eu.software4you.ulib.core.api.dependencies.DependencyLoader;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.sql.SqlEngine;
import eu.software4you.ulib.core.api.util.value.LazyValue;
import eu.software4you.ulib.core.impl.configuration.yaml.YamlSerializer;
import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.spigot.api.plugin.ExtendedJavaPlugin;
import eu.software4you.ulib.spigot.impl.configuration.BukkitSerialisationAdapter;
import eu.software4you.ulib.spigot.impl.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.spigot.impl.usercache.MainUserCacheImpl;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
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
        PLAIN_MC_VERSION = new LazyValue<>(() -> (String) ReflectUtil.call(server.getClass(), server, "getServer().getVersion()"));

        YamlSerializer.getInstance().getAdapters().registerAdapter(ConfigurationSerializable.class, new BukkitSerialisationAdapter());

        System.setProperty(PROP_KEY, "cinit");
    }

    private final Plugin plugin;

    public PluginSubst(Plugin plugin, JavaPluginLoader loader, PluginDescriptionFile descriptionFile, File dataFolder, File file) {
        super(loader, descriptionFile, dataFolder, file);
        this.plugin = plugin;
    }

    private ProxyServerBridgeImpl proxyServerBridgeImpl;
    private SqlEngine mainUserCacheEngine;


    public static PluginSubst getInstance() {
        return instance;
    }

    public static String getPlainMcVersion() {
        return PLAIN_MC_VERSION.get();
    }

    @Override
    public void onEnable() {
        try {
            getConf().setConversionPolicy(ConversionPolicy.THROW_EXCEPTION);

            registerEvents(instance = this);


            ProxyServerBridgeImpl.init(this);
            proxyServerBridgeImpl = (ProxyServerBridgeImpl) ProxyServerBridge.getInstance();
            registerEvents(proxyServerBridgeImpl);

            Messenger messenger = Bukkit.getMessenger();
            messenger.registerOutgoingPluginChannel(this, ProxyServerBridge.CHANNEL);
            messenger.registerIncomingPluginChannel(this, ProxyServerBridge.CHANNEL, proxyServerBridgeImpl);
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
            messenger.registerIncomingPluginChannel(this, "BungeeCord", proxyServerBridgeImpl);


            MainUserCacheImpl.init(this, mainUserCacheEngine = new SqlEngine());
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
        if (mainUserCacheEngine.isConnected()) {
            mainUserCacheEngine.disconnect();
        }

        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);

        System.setProperty(PROP_KEY, "disabled");
    }

    @EventHandler
    public void handle(PluginDisableEvent e) {
        DependencyLoader.free(e.getPlugin().getClass().getClassLoader());
    }

    public boolean isListening(Class<? extends Listener> clazz) {
        return HandlerList.getRegisteredListeners(this).stream()
                .map(reg -> reg.getListener().getClass())
                .anyMatch(cl -> cl == clazz);
    }

    public void makeBukkitAvailable() {
        var caller = ReflectUtil.getCallerClass();
        if (caller.getModule() != getClass().getModule())
            throw new IllegalCallerException();

        getClass().getModule().addExports(caller.getPackageName(), Bukkit.class.getModule());
    }

    @Override
    public @NotNull Plugin getPluginObject() {
        return plugin;
    }
}
