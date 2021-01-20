package eu.software4you.ulib;

import eu.software4you.aether.Dependencies;
import eu.software4you.aether.Repository;
import eu.software4you.common.collection.Pair;
import eu.software4you.spigot.PlugMan;
import eu.software4you.spigot.enchantment.CustomEnchantmentHandler;
import eu.software4you.spigot.inventorymenu.MenuManager;
import eu.software4you.spigot.inventorymenu.MenuManagerListener;
import eu.software4you.spigot.inventorymenu.factory.EntryFactory;
import eu.software4you.spigot.inventorymenu.factory.EntryFactoryImpl;
import eu.software4you.spigot.inventorymenu.factory.MenuFactory;
import eu.software4you.spigot.inventorymenu.factory.MenuFactoryImpl;
import eu.software4you.spigot.plugin.ExtendedJavaPlugin;
import eu.software4you.ulib.minecraft.proxybridge.SBB;
import eu.software4you.ulib.minecraft.proxybridge.SpigotSBB;
import eu.software4you.ulib.minecraft.usercache.MainUserCache;
import eu.software4you.ulib.minecraft.usercache.UserCache;
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

        Properties.getInstance().ADDITIONAL_LIBS.put("com.github.cryptomorin:XSeries:7.5.5",
                new Pair<>("com.cryptomorin.xseries.XMaterial", Repository.MAVEN_CENTRAL));
        ULib.makeReady();
    }

    private SpigotSBB spigotBungeeCordBridge;

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


            spigotBungeeCordBridge = new SpigotSBB(this);
            registerEvents(spigotBungeeCordBridge);
            SBB.setInstance(spigotBungeeCordBridge);

            Messenger messenger = Bukkit.getMessenger();
            messenger.registerOutgoingPluginChannel(this, SBB.CHANNEL);
            messenger.registerIncomingPluginChannel(this, SBB.CHANNEL, spigotBungeeCordBridge);
            messenger.registerOutgoingPluginChannel(this, "BungeeCord");
            messenger.registerIncomingPluginChannel(this, "BungeeCord", spigotBungeeCordBridge);


            EntryFactory.setInstance(new EntryFactoryImpl());
            MenuFactory.setInstance(new MenuFactoryImpl());

            MenuManager.setHandlerFunction(MenuManagerListener::new);

            registerEvents(new CustomEnchantmentHandler());

            UserCache.setImpl(UserCacheImpl.class);
            MainUserCache.setPlugin(this);

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
