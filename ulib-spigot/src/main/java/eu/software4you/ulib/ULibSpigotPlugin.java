package eu.software4you.ulib;

import eu.software4you.aether.Repository;
import eu.software4you.common.collection.Pair;
import eu.software4you.spigot.PlugMan;
import eu.software4you.spigot.enchantment.CustomEnchantmentHandler;
import eu.software4you.spigot.inventorymenu.MenuManagerInit;
import eu.software4you.spigot.inventorymenu.MenuManagerListener;
import eu.software4you.spigot.inventorymenu.factory.EntryFactoryImpl;
import eu.software4you.spigot.inventorymenu.factory.EntryFactoryInit;
import eu.software4you.spigot.inventorymenu.factory.MenuFactoryImpl;
import eu.software4you.spigot.inventorymenu.factory.MenuFactoryInit;
import eu.software4you.spigot.plugin.ExtendedJavaPlugin;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeImpl;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridgeInit;
import eu.software4you.ulib.minecraft.usercache.UserCacheInit;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.Messenger;

public class ULibSpigotPlugin extends ExtendedJavaPlugin {
    public static final boolean PAPER;
    private static final String PROP_KEY = "eu.software4you.minecraft.ulib_spigot";
    private static ULibSpigotPlugin instance = null;

    static {
        boolean paper;
        try {
            Class.forName("com.destroystokyo.paper.Title");
            paper = true;
        } catch (ClassNotFoundException e) {
            paper = false;
        }
        PAPER = paper;

        // trick the shade plugin
        char[] clazz = {'c', 'o', 'm',
                '.', 'c', 'r', 'y', 'p', 't', 'o', 'm', 'o', 'r', 'i', 'n',
                '.', 'x', 's', 'e', 'r', 'i', 'e', 's',
                '.', 'X', 'M', 'a', 't', 'e', 'r', 'i', 'a', 'l'};
        Properties.getInstance().ADDITIONAL_LIBS.put("com.github.cryptomorin:XSeries:7.5.5",
                new Pair<>(new String(clazz), Repository.MAVEN_CENTRAL));
        ULib.makeReady();
    }

    private ProxyServerBridgeImpl proxyServerBridgeImpl;

    public static ULibSpigotPlugin getInstance() {
        return instance;
    }

    @Override
    public void onLoad() {
        if (System.getProperties().containsKey(PROP_KEY)) {
            getLogger().severe("A previous uLib instance that has not been fully removed was detected." +
                    " This could lead to unexpected behaviour!" +
                    " It is recommended to fully stop the server instead of doing a reload," +
                    " or reload specific plugins with a plugin-manager.");
        }
    }

    @Override
    public void onEnable() {
        try {
            instance = this;


            proxyServerBridgeImpl = new ProxyServerBridgeImpl(this);
            registerEvents(proxyServerBridgeImpl);
            ProxyServerBridgeInit.proxyServerBridge(proxyServerBridgeImpl);

            Messenger messenger = Bukkit.getMessenger();
            messenger.registerOutgoingPluginChannel(this, ProxyServerBridge.CHANNEL);
            messenger.registerIncomingPluginChannel(this, ProxyServerBridge.CHANNEL, proxyServerBridgeImpl);
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
            messenger.registerIncomingPluginChannel(this, "BungeeCord", proxyServerBridgeImpl);


            EntryFactoryInit.entryFactory(new EntryFactoryImpl());
            MenuFactoryInit.menuFactory(new MenuFactoryImpl());

            MenuManagerInit.menuManager(MenuManagerListener::new);

            registerEvents(new CustomEnchantmentHandler());

            UserCacheInit.userCache(UserCacheImpl.class);
            UserCacheInit.pluginBase(this);

            if (!PAPER) {
                getLogger().warning("This server does not run on paper, some features may not be available!" +
                        " Consider switching to paper, tuinity or yatopia, they provide better performance, bug fixes and more features." +
                        " See https://papermc.io/ for more information.");
                // register no-paper workarounds
                registerEvents(new CustomEnchantmentHandler.NoPaper());

                return;
            }


            // only for paper from this point on
            registerEvents(new CustomEnchantmentHandler.Paper());

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        System.setProperty(PROP_KEY, "enabled");
    }

    @Override
    public void onDisable() {
        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);

        System.setProperty(PROP_KEY, "disabled");
    }

    @Override
    protected void finalize() throws Throwable {
        System.getProperties().remove(PROP_KEY);
    }

    private boolean reinitiate() {
        if (!PlugMan.reload(this)) {
            try {
                Bukkit.reload();
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }


}
