package eu.software4you.ulib.bungeecord.player;

import eu.software4you.ulib.core.impl.BypassAnnotationEnforcement;
import eu.software4you.ulib.minecraft.usercache.UserCache;
import net.md_5.bungee.api.*;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.*;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.score.Scoreboard;
import org.jetbrains.annotations.*;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.*;

/**
 * A dummy player intended for use if the actual player is current offline.
 */
@BypassAnnotationEnforcement
public class DummyProxiedPlayer implements ProxiedPlayer {
    private final UUID uniqueId;
    private final String name;

    public DummyProxiedPlayer(@NotNull UUID uniqueId) {
        this(uniqueId, UserCache.isMainCache()
                ? UserCache.getMainCache().getUsername(uniqueId).orElse(null) : null);
    }

    public DummyProxiedPlayer(@NotNull UUID uniqueId, @Nullable String name) {
        this.uniqueId = uniqueId;
        this.name = name;
    }

    @Override
    @Nullable
    public String getUUID() {
        return uniqueId.toString();
    }

    @Override
    @NotNull
    public UUID getUniqueId() {
        return uniqueId;
    }

    @Override
    @Contract("-> fail")
    public String getDisplayName() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void setDisplayName(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent... baseComponents) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void sendMessage(ChatMessageType chatMessageType, BaseComponent baseComponent) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void sendMessage(UUID sender, BaseComponent... message) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void sendMessage(UUID sender, BaseComponent message) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void connect(ServerInfo serverInfo) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void connect(ServerInfo serverInfo, ServerConnectEvent.Reason reason) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _, _ -> fail")
    public void connect(ServerInfo serverInfo, Callback<Boolean> callback, ServerConnectEvent.Reason reason) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void connect(ServerConnectRequest serverConnectRequest) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public Server getServer() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public int getPing() {
        return 0;
    }

    @Override
    @Contract("_, _ -> fail")
    public void sendData(String s, byte[] bytes) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public PendingConnection getPendingConnection() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void chat(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public ServerInfo getReconnectServer() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void setReconnectServer(ServerInfo serverInfo) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public Locale getLocale() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    public byte getViewDistance() {
        return 0;
    }

    @Override
    @Contract("-> fail")
    public ChatMode getChatMode() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> false")
    public boolean hasChatColors() {
        return false;
    }

    @Override
    @Contract("-> fail")
    public SkinConfiguration getSkinParts() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public MainHand getMainHand() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void setTabHeader(BaseComponent baseComponent, BaseComponent baseComponent1) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_, _ -> fail")
    public void setTabHeader(BaseComponent[] baseComponents, BaseComponent[] baseComponents1) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public void resetTabHeader() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void sendTitle(Title title) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> false")
    public boolean isForgeUser() {
        return false;
    }

    @Override
    @Contract("-> fail")
    public Map<String, String> getModList() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public Scoreboard getScoreboard() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Nullable
    public String getName() {
        return name;
    }

    @Override
    @Contract("_ -> fail")
    public void sendMessage(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void sendMessages(String... strings) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void sendMessage(BaseComponent... baseComponents) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void sendMessage(BaseComponent baseComponent) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public Collection<String> getGroups() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void addGroups(String... strings) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void removeGroups(String... strings) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> false")
    public boolean hasPermission(String s) {
        return false;
    }

    @Override
    @Contract("_, _ -> fail")
    public void setPermission(String s, boolean b) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public Collection<String> getPermissions() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Nullable
    public String toString() {
        return name;
    }

    @Override
    @Contract("-> fail")
    public InetSocketAddress getAddress() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> fail")
    public SocketAddress getSocketAddress() {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void disconnect(String s) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void disconnect(BaseComponent... baseComponents) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("_ -> fail")
    public void disconnect(BaseComponent baseComponent) {
        throw new UnsupportedOperationException("dummy");
    }

    @Override
    @Contract("-> false")
    public boolean isConnected() {
        return false;
    }

    @Override
    @Contract("-> fail")
    public Unsafe unsafe() {
        throw new UnsupportedOperationException("dummy");
    }
}
