package eu.software4you.ulib.bungeecord.api.player;

import eu.software4you.ulib.minecraft.api.proxybridge.ProxyServerBridge;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ULibProxyPlayer {
    private final ProxiedPlayer player;

    public ULibProxyPlayer(ProxiedPlayer player) {
        this.player = player;
    }

    public void sendSoundEffectAndBlock(String sound, float volume, float pitch) {
        ServerInfo info;
        if (player.getServer() != null) {
            info = player.getServer().getInfo();
        } else if (player.getReconnectServer() != null) {
            info = player.getReconnectServer();
        } else {
            info = ProxyServer.getInstance().getReconnectHandler().getServer(player);
        }
        ProxyServerBridge.getInstance().trigger(info.getName(), "PlayerPlaySoundEffect " + player.getName() + " " + sound + " " + volume + " " + pitch);
    }

}