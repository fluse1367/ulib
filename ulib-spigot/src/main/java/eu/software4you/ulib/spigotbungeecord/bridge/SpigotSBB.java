package eu.software4you.ulib.spigotbungeecord.bridge;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.software4you.minecraft.plugin.ExtendedPlugin;
import eu.software4you.ulib.ULibPlugin;
import eu.software4you.ulib.spigotbungeecord.bridge.message.Message;
import eu.software4you.ulib.spigotbungeecord.bridge.message.MessageType;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Future;

public class SpigotSBB extends SBB implements PluginMessageListener, Listener {
    private final ExtendedPlugin plugin;
    private String thisServer = null;
    private String lastReceivedRequest;
    private String lastReceivedCommand;

    @SneakyThrows
    public SpigotSBB(ExtendedPlugin plugin) {
        this.plugin = plugin;
    }

    private void sendMessage(String server, Message message) {
        byte[] data = new Gson().toJson(message).getBytes();
        Bukkit.getOnlinePlayers().stream().findFirst().ifPresent(server.equals(PROXY_SERVER_NAME) ?
                player -> player.sendPluginMessage(plugin, CHANNEL, data) :
                player -> {
                    // plugin message forwarding, see https://www.spigotmc.org/wiki/bukkit-bungee-plugin-messaging-channel/#forward
                    ByteArrayDataOutput out = ByteStreams.newDataOutput();
                    out.writeUTF("Forward");
                    out.writeUTF(server);
                    out.writeUTF(CHANNEL);

                    out.writeShort(data.length);
                    out.write(data);

                    player.sendPluginMessage(plugin, "BungeeCord", out.toByteArray());
                });
    }

    @Override
    public Future<byte[]> request(String targetServer, String line, long timeout) {
        Message message = new Message(UUID.randomUUID(), thisServer, MessageType.REQUEST, line.getBytes(StandardCharsets.UTF_8));
        sendMessage(targetServer, message);
        return awaitData(message.getId(), timeout);
    }

    @Override
    public Future<byte[]> request(String line, long timeout) {
        if (lastReceivedRequest == null)
            throw new IllegalStateException("No target server known");
        return request(lastReceivedRequest, line, timeout);
    }

    @Override
    public void trigger(String targetServer, String line) {
        sendMessage(targetServer, new Message(UUID.randomUUID(), thisServer, MessageType.COMMAND, line.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void trigger(String line) {
        if (lastReceivedCommand == null)
            throw new IllegalStateException("No target server known");
        trigger(lastReceivedCommand, line);
    }

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] data) {
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

        Message message = new Gson().fromJson(je, Message.class);
        String from = message.getFrom();
        switch (message.getType()) {
            case REQUEST:
                String line = new String(message.getData(), StandardCharsets.UTF_8);
                byte[] result = parseCommand(line).execute();
                sendMessage(from, new Message(message.getId(), thisServer, MessageType.ANSWER, result));
                lastReceivedRequest = from;
                break;
            case COMMAND:
                line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).execute();
                lastReceivedCommand = from;
                break;
            case ANSWER:
                putData(message.getId(), message.getData());
                break;
        }
    }

    // attempt to get own server name
    @EventHandler
    public void handle(PlayerJoinEvent e) {
        if (thisServer != null)
            return;
        ULibPlugin.getInstance().async(this::attemptSetThisServer);
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
