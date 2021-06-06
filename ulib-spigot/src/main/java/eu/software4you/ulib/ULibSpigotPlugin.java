package eu.software4you.ulib;

import eu.software4you.common.collection.Pair;
import eu.software4you.dependencies.DependencyLoader;
import eu.software4you.spigot.plugin.ExtendedJavaPlugin;
import eu.software4you.sql.SqlEngine;
import eu.software4you.ulib.impl.spigot.enchantment.CustomEnchantmentHandler;
import eu.software4you.ulib.impl.spigot.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.impl.spigot.usercache.MainUserCacheImpl;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridge;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.messaging.Messenger;

public class ULibSpigotPlugin extends ExtendedJavaPlugin implements Listener {
    public static final boolean PAPER;
    private static final String PROP_KEY = "ulib.plugin_status";
    private static ULibSpigotPlugin instance = null;

    static {
        // check for already existing instance
        if (System.getProperties().containsKey(PROP_KEY)) {
            System.err.println("[uLib] A previous uLib instance was detected. uLib does not support being reloaded." +
                    " Please fully stop the server instead of doing a reload, or reload specific plugins with a plugin-manager.");
            throw new IllegalStateException("Reloading not supported");
        }

        boolean paper;
        try {
            Class.forName("com.destroystokyo.paper.Title");
            paper = true;
        } catch (ClassNotFoundException e) {
            paper = false;
        }
        PAPER = paper;

        Properties.getInstance().ADDITIONAL_LIBS.add(new Pair<>("{{maven.xseries}}", "central"));
        ULib.init();

        System.setProperty(PROP_KEY, "cinit");
    }

    private ProxyServerBridgeImpl proxyServerBridgeImpl;
    private SqlEngine mainUserCacheEngine;
    private String plainMcVersion;

    public static ULibSpigotPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        try {
            registerEvents(instance = this);


            proxyServerBridgeImpl = ProxyServerBridgeImpl.init(this);
            registerEvents(proxyServerBridgeImpl);

            Messenger messenger = Bukkit.getMessenger();
            messenger.registerOutgoingPluginChannel(this, ProxyServerBridge.CHANNEL);
            messenger.registerIncomingPluginChannel(this, ProxyServerBridge.CHANNEL, proxyServerBridgeImpl);
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
            messenger.registerIncomingPluginChannel(this, "BungeeCord", proxyServerBridgeImpl);

            registerEvents(new CustomEnchantmentHandler());


            MainUserCacheImpl.init(this, mainUserCacheEngine = new SqlEngine());

            if (PAPER) {
                registerEvents(new CustomEnchantmentHandler.Paper());

                plainMcVersion = getServer().getMinecraftVersion();

            } else {
                getLogger().warning("This server does not run on paper, some features may not be available!" +
                        " Consider switching to purpur, airplane, tuinity or paper as they provide better performance, bug fixes and more features." +
                        " See https://papermc.io/ for more information.");
                // register no-paper workarounds
                registerEvents(new CustomEnchantmentHandler.NoPaper());

                plainMcVersion = (String) ReflectUtil.call(getServer().getClass(), getServer(), "getServer().getVersion()");
            }
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

    @Override
    protected void finalize() throws Throwable {
        System.getProperties().remove(PROP_KEY);
        super.finalize();
    }

    @EventHandler
    public void handle(PluginDisableEvent e) {
        DependencyLoader.free(e.getPlugin().getClass().getClassLoader());
    }

    public String getPlainMcVersion() {
        return plainMcVersion;
    }
}
