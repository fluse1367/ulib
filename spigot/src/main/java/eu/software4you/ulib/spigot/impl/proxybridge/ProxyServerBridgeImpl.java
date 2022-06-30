package eu.software4you.ulib.spigot.impl.proxybridge;

import com.google.common.io.*;
import com.google.gson.*;
import eu.software4you.ulib.core.util.Unsafe;
import eu.software4you.ulib.minecraft.impl.SharedConstants;
import eu.software4you.ulib.minecraft.impl.proxybridge.AbstractProxyServerBridge;
import eu.software4you.ulib.minecraft.proxybridge.message.Message;
import eu.software4you.ulib.minecraft.proxybridge.message.MessageType;
import eu.software4you.ulib.spigot.impl.DelegationListener;
import eu.software4you.ulib.spigot.impl.PluginSubst;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Future;

public final class ProxyServerBridgeImpl extends AbstractProxyServerBridge implements PluginMessageListener {
    private final PluginSubst plugin;
    private String thisServer = null;
    private String lastReceivedRequest;
    private String lastReceivedCommand;

    public ProxyServerBridgeImpl(PluginSubst plugin) {
        this.plugin = plugin;
        DelegationListener.registerSingleDelegation(PlayerJoinEvent.class, this::handle);
    }

    private void sendMessage(String server, Message message) {
        byte[] data = Unsafe.doPrivileged(() -> new Gson().toJson(message).getBytes());
        Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(server.equals(PROXY_SERVER_NAME) ?
                player -> player.sendPluginMessage(plugin.getPluginObject(), CHANNEL, data) :
                player -> {
                    // plugin message forwarding, see https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Forward");
                    out.writeUTF(server);
                    out.writeUTF(CHANNEL);

                    out.writeShort(data.length);
                    out.write(data);

                    player.sendPluginMessage(plugin.getPluginObject(), "BungeeCord", out.toByteArray());
                });
    }

    @Override
    public @NotNull Future<byte[]> request(@NotNull String targetServer, @NotNull String line, long timeout) {
        Message message = new Message(UUID.randomUUID(), thisServer, MessageType.REQUEST, line.getBytes(StandardCharsets.UTF_8));
        sendMessage(targetServer, message);
        return awaitData(message.getId(), timeout);
    }

    @Override
    public @NotNull Future<byte[]> request(@NotNull String line, long timeout) {
        if (lastReceivedRequest == null)
            throw new IllegalStateException("No target server known");
        return request(lastReceivedRequest, line, timeout);
    }

    @Override
    public void trigger(@NotNull String targetServer, @NotNull String line) {
        sendMessage(targetServer, new Message(UUID.randomUUID(), thisServer, MessageType.COMMAND, line.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void trigger(@NotNull String line) {
        if (lastReceivedCommand == null)
            throw new IllegalStateException("No target server known");
        trigger(lastReceivedCommand, line);
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, byte[] data) {
        if (channel.equals("BungeeCord")) {
            ByteArrayDataInput in = ByteStreams.newDataInput(data);
            if (!in.readUTF().equals(CHANNEL)) // sub-channel check
                return;
            byte[] msgBytes = new byte[in.readShort()];
            in.readFully(msgBytes);
            data = msgBytes;
        } else if (!channel.equals(CHANNEL))
            return;

        JsonElement je = JsonParser.parseString(new String(data, StandardCharsets.UTF_8));
        if (!je.isJsonObject())
            return;

        Message message = Unsafe.doPrivileged(() -> new Gson().fromJson(je, Message.class));
        String from = message.getFrom();
        switch (message.getType()) {
            case REQUEST:
                String line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).ifPresent(pc -> {
                    byte[] result = pc.execute(from);
                    sendMessage(from, new Message(message.getId(), PROXY_SERVER_NAME, MessageType.ANSWER, result));
                    lastReceivedRequest = from;
                });
                break;
            case COMMAND:
                line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).ifPresent(pc -> {
                    pc.execute(from);
                    lastReceivedCommand = from;
                });
                break;
            case ANSWER:
                putData(message.getId(), message.getData());
                break;
        }
    }

    // attempt to get own server name
    public void handle(PlayerJoinEvent e) {
        if (thisServer != null)
            return;
        SharedConstants.BASE.get().async(this::attemptSetThisServer);
    }

    @SneakyThrows // possible exception thrown in Future#get effectively never happens (see SBB.DataSupplier#get)
    private void attemptSetThisServer() {
        if (thisServer != null)
            return;
        thisServer = ""; // block other attempts
        byte[] bytes = request(PROXY_SERVER_NAME, "ServerName", 5000).get();
        thisServer = bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
    }

}
