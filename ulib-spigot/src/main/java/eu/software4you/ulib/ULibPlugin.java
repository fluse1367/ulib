package eu.software4you.ulib;

import eu.software4you.minecraft.PlugMan;
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
    private static ULibPlugin instance = null;

    static {
        Properties.getInstance().ADDITIONAL_LIBS.put("com.github.cryptomorin:XSeries:7.2.1.2", "com.cryptomorin.xseries.XMaterial");
        ULib.makeReady();
    }

    private SpigotSBB spigotBungeeCordBridge;

    public static ULibPlugin getInstance() {
        return instance;
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

        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        Messenger messenger = Bukkit.getMessenger();
        messenger.unregisterIncomingPluginChannel(this);
        messenger.unregisterOutgoingPluginChannel(this);
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
