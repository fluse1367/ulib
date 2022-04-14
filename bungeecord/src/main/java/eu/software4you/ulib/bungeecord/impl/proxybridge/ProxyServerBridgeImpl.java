package eu.software4you.ulib.bungeecord.impl.proxybridge;

import com.google.gson.*;
import eu.software4you.ulib.bungeecord.plugin.ExtendedPlugin;
import eu.software4you.ulib.minecraft.impl.proxybridge.AbstractProxyServerBridge;
import eu.software4you.ulib.minecraft.proxybridge.message.Message;
import eu.software4you.ulib.minecraft.proxybridge.message.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Future;

@RequiredArgsConstructor
public class ProxyServerBridgeImpl extends AbstractProxyServerBridge implements Listener {
    private final ExtendedPlugin plugin;
    private ServerInfo lastReceivedRequest = null;
    private ServerInfo lastReceivedCommand = null;

    private void sendMessage(ServerInfo server, Message message) {
        server.sendData(CHANNEL, new Gson().toJson(message).getBytes());
    }

    private ServerInfo findServer(String serverName) {
        ServerInfo server = plugin.getProxy().getServerInfo(serverName);
        if (server == null)
            throw new IllegalArgumentException(String.format("Server %s was not found or is not connected", serverName));
        return server;
    }

    @Override
    public Future<byte[]> request(String targetServer, String line, final long timeout) {
        ServerInfo server = findServer(targetServer);
        Message message = new Message(UUID.randomUUID(), null, MessageType.REQUEST, line.getBytes(StandardCharsets.UTF_8));
        sendMessage(server, message);
        return awaitData(message.getId(), timeout);
    }

    @Override
    public Future<byte[]> request(String line, long timeout) {
        if (lastReceivedRequest == null)
            throw new IllegalStateException("No target server known");
        return request(lastReceivedRequest.getName(), line, timeout);
    }

    @Override
    public void trigger(String targetServer, String line) {
        ServerInfo server = findServer(targetServer);
        sendMessage(server, new Message(UUID.randomUUID(), null, MessageType.COMMAND, line.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void trigger(String line) {
        if (lastReceivedCommand == null)
            throw new IllegalStateException("No target server known");
        sendMessage(lastReceivedCommand, new Message(UUID.randomUUID(), null, MessageType.COMMAND, line.getBytes(StandardCharsets.UTF_8)));
    }

    @SneakyThrows
    @EventHandler
    public void handle(PluginMessageEvent e) {
        if (!e.getTag().equals(CHANNEL))
            return;

        JsonElement je = JsonParser.parseString(new String(e.getData(), StandardCharsets.UTF_8));
        if (!je.isJsonObject())
            return;

        ServerInfo from = ((ProxiedPlayer) e.getSender()).getServer().getInfo();

        Message message = new Gson().fromJson(je, Message.class);

        switch (message.getType()) {
            case REQUEST:
                String line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).ifPresent(pc -> {
                    byte[] result = pc.execute(from.getName());
                    sendMessage(from, new Message(message.getId(), PROXY_SERVER_NAME, MessageType.ANSWER, result));
                    lastReceivedRequest = from;
                });
                break;
            case COMMAND:
                line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).ifPresent(pc -> {
                    pc.execute(from.getName());
                    lastReceivedCommand = from;
                });
                break;
            case ANSWER:
                putData(message.getId(), message.getData());
                break;
        }
    }
}
