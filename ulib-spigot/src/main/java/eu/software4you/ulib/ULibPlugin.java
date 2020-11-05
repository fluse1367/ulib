package eu.software4you.ulib;

import eu.software4you.minecraft.PlugMan;
import eu.software4you.minecraft.enchantment.CustomEnchantmentHandler;
import eu.software4you.minecraft.inventorymenu.MenuManager;
import eu.software4you.minecraft.inventorymenu.MenuManagerListener;
import eu.software4you.minecraft.inventorymenu.factory.EntryFactory;
import eu.software4you.minecraft.inventorymenu.factory.EntryFactoryImpl;
import eu.software4you.minecraft.inventorymenu.factory.MenuFactory;
import eu.software4you.minecraft.inventorymenu.factory.MenuFactoryImpl;
import eu.software4you.minecraft.plugin.ExtendedJavaPlugin;
import eu.software4you.ulib.spigotbungeecord.bridge.SBB;
import eu.software4you.ulib.spigotbungeecord.bridge.SpigotSBB;
import org.bukkit.Bukkit;
import org.bukkit.plugin.messaging.Messenger;

public class ULibPlugin extends ExtendedJavaPlugin {
    public static final boolean PAPER;
    private static final String PROP_KEY = "eu.software4you.minecraft.ulib_spigot";
    private static ULibPlugin instance = null;

    static {
        boolean paper;
        try {
            Class.forName("com.destroystokyo.paper.Title");
            paper = true;
        } catch (ClassNotFoundException e) {
            paper = false;
        }
        PAPER = paper;

        Properties.getInstance().ADDITIONAL_LIBS.put("com.github.cryptomorin:XSeries:7.5.5", "com.cryptomorin.xseries.XMaterial");
        ULib.makeReady();
    }

    private SpigotSBB spigotBungeeCordBridge;

    public static ULibPlugin getInstance() {
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

            if (!PAPER) {
                getLogger().warning("This server does not run on paper, some features may not be available!" +
                        " Consider switching to paper (or tuinity), it provides better performance, bug fixes and more features." +
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
