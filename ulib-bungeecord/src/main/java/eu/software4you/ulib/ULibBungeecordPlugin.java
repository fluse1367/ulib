package eu.software4you.ulib;

import eu.software4you.bungeecord.plugin.ExtendedProxyPlugin;
import eu.software4you.ulib.minecraft.proxybridge.BungeeCordSBB;
import eu.software4you.ulib.minecraft.proxybridge.SBB;
import eu.software4you.ulib.minecraft.usercache.MainUserCache;
import eu.software4you.ulib.minecraft.usercache.UserCache;

public class ULibBungeecordPlugin extends ExtendedProxyPlugin {
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

            UserCache.setImpl(UserCacheImpl.class);
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
