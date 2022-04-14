package eu.software4you.ulib.bungeecord.player;

import eu.software4you.ulib.minecraft.usercache.UserCache;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

/**
 * A dummy player intended for use if the actual player is current offline.
 */
public class DummyProxiedPlayer implements ProxiedPlayer {
    private final UUID uniqueId;
    private final String name;

    public DummyProxiedPlayer(UUID uniqueId) {
        this(uniqueId, UserCache.isMainCache()
                ? UserCache.getMainCache().getUsername(uniqueId) : null);
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
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void setDisplayName(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent baseComponent) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessage(UUID sender, BaseComponent... message) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessage(UUID sender, BaseComponent message) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void connect(ServerInfo serverInfo) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void connect(ServerInfo serverInfo, ServerConnectEvent.Reason reason) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {
        throw new UnsupportedOperationException("dummy");
    }

    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, boolean b) {
        throw new UnsupportedOperationException("dummy");
    }

    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, boolean b, int i) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        throw new UnsupportedOperationException("dummy");
    }

    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, boolean b, ServerConnectEvent.Reason reason, int i) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void connect(ServerConnectRequest serverConnectRequest) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public Server getServer() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    public void sendData(String s, byte[] bytes) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public PendingConnection getPendingConnection() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void chat(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public ServerInfo getReconnectServer() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void setReconnectServer(ServerInfo serverInfo) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public byte getViewDistance() {
        return 0;
    }

    @Override
    public ChatMode getChatMode() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public boolean hasChatColors() {
        return false;
    }

    @Override
    public SkinConfiguration getSkinParts() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public MainHand getMainHand() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void setTabHeader(BaseComponent baseComponent, BaseComponent baseComponent1) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void setTabHeader(BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void resetTabHeader() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendTitle(Title title) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public boolean isForgeUser() {
        return false;
    }

    @Override
    public Map<String, String> getModList() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public Scoreboard getScoreboard() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void sendMessage(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessages(String... strings) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessage(BaseComponent... baseComponents) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void sendMessage(BaseComponent baseComponent) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public Collection<String> getGroups() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void addGroups(String... strings) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void removeGroups(String... strings) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    public void setPermission(String s, boolean b) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public Collection<String> getPermissions() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public InetSocketAddress getAddress() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public SocketAddress getSocketAddress() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void disconnect(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void disconnect(BaseComponent... baseComponents) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public void disconnect(BaseComponent baseComponent) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public boolean isConnected() {
        return false;
    }

    @Override
    public Unsafe unsafe() {
        throw new UnsupportedOperationException("dummy");
    }
}