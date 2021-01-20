package eu.software4you.bungeecord.player;

import eu.software4you.ulib.minecraft.usercache.MainUserCache;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;
import org.apache.commons.lang.NotImplementedException;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class DummyProxiedPlayer implements ProxiedPlayer {
    private final UUID uniqueId;
    private final String name;

    public DummyProxiedPlayer(UUID uniqueId) {
        this(uniqueId, MainUserCache.isEnabled()
                ? MainUserCache.get().getUsername(uniqueId) : null);
    }

    public DummyProxiedPlayer(UUID uniqueId, String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Override
    public String getUUID() {
        return uniqueId.toString();
    }

    @Override
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    public String getDisplayName() {
        throw new NotImplementedException();
    }

    @Override
    public void setDisplayName(String s) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent baseComponent) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(UUID sender, BaseComponent... message) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(UUID sender, BaseComponent message) {
        throw new NotImplementedException();
    }

    @Override
    public void connect(ServerInfo serverInfo) {
        throw new NotImplementedException();
    }

    @Override
    public void connect(ServerInfo serverInfo, ServerConnectEvent.Reason reason) {
        throw new NotImplementedException();
    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {
        throw new NotImplementedException();
    }

    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, boolean b) {
        throw new NotImplementedException();
    }

    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, boolean b, int i) {
        throw new NotImplementedException();
    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        throw new NotImplementedException();
    }

    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, boolean b, ServerConnectEvent.Reason reason, int i) {
        throw new NotImplementedException();
    }

    @Override
    public void connect(ServerConnectRequest serverConnectRequest) {
        throw new NotImplementedException();
    }

    @Override
    public Server getServer() {
        throw new NotImplementedException();
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sendData(String s, byte[] bytes) {
        throw new NotImplementedException();
    }

    @Override
    public PendingConnection getPendingConnection() {
        throw new NotImplementedException();
    }

    @Override
    public void chat(String s) {
        throw new NotImplementedException();
    }

    @Override
    public ServerInfo getReconnectServer() {
        throw new NotImplementedException();
    }

    @Override
    public void setReconnectServer(ServerInfo serverInfo) {
        throw new NotImplementedException();
    }

    @Override
    public Locale getLocale() {
        throw new NotImplementedException();
    }

    @Override
    public byte getViewDistance() {
        return 0;
    }

    @Override
    public ChatMode getChatMode() {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasChatColors() {
        return false;
    }

    @Override
    public SkinConfiguration getSkinParts() {
        throw new NotImplementedException();
    }

    @Override
    public MainHand getMainHand() {
        throw new NotImplementedException();
    }

    @Override
    public void setTabHeader(BaseComponent baseComponent, BaseComponent baseComponent1) {
        throw new NotImplementedException();
    }

    @Override
    public void setTabHeader(BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {
        throw new NotImplementedException();
    }

    @Override
    public void resetTabHeader() {
        throw new NotImplementedException();
    }

    @Override
    public void sendTitle(Title title) {
        throw new NotImplementedException();
    }

    @Override
    public boolean isForgeUser() {
        return false;
    }

    @Override
    public Map<String, String> getModList() {
        throw new NotImplementedException();
    }

    @Override
    public Scoreboard getScoreboard() {
        throw new NotImplementedException();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(String s) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessages(String... strings) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        throw new NotImplementedException();
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<String> getGroups() {
        throw new NotImplementedException();
    }

    @Override
    public void addGroups(String... strings) {
        throw new NotImplementedException();
    }

    @Override
    public void removeGroups(String... strings) {
        throw new NotImplementedException();
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public void setPermission(String s, boolean b) {
        throw new NotImplementedException();
    }

    @Override
    public Collection<String> getPermissions() {
        throw new NotImplementedException();
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public InetSocketAddress getAddress() {
        throw new NotImplementedException();
    }

    @Override
    public SocketAddress getSocketAddress() {
        throw new NotImplementedException();
    }

    @Override
    public void disconnect(String s) {
        throw new NotImplementedException();
    }

    @Override
    public void disconnect(BaseComponent... baseComponents) {
        throw new NotImplementedException();
    }

    @Override
    public void disconnect(BaseComponent baseComponent) {
        throw new NotImplementedException();
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Unsafe unsafe() {
        throw new NotImplementedException();
    }
}
