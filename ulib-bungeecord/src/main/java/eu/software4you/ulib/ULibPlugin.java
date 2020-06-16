package eu.software4you.ulib;

import eu.software4you.proxy.plugin.ExtendedProxyPlugin;
import eu.software4you.proxy.usercache.MainUserCache;
import eu.software4you.ulib.spigotbungeecord.bridge.BungeeCordSBB;
import eu.software4you.ulib.spigotbungeecord.bridge.SBB;

public class ULibPlugin extends ExtendedProxyPlugin {
    static {
        ULib.makeReady();
    }

    private BungeeCordSBB spigotBungeeCordBridge;

    @Override
    public void onEnable() {
        try {
            spigotBungeeCordBridge = new BungeeCordSBB(this);
            SBB.setInstance(spigotBungeeCordBridge);
            registerEvents(spigotBungeeCordBridge);
            getProxy().registerChannel(spigotBungeeCordBridge.CHANNEL);


            MainUserCache.setPlugin(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        if (spigotBungeeCordBridge != null) {
            getProxy().unregisterChannel(spigotBungeeCordBridge.CHANNEL);
            unregisterEvents(spigotBungeeCordBridge);
        }
    }
}
