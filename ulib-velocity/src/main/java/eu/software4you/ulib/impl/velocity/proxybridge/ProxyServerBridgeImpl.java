package eu.software4you.ulib.impl.velocity.proxybridge;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.ChannelIdentifier;
import com.velocitypowered.api.proxy.messages.ChannelMessageSink;
import com.velocitypowered.api.proxy.messages.LegacyChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import eu.software4you.ulib.ImplInjector;
import eu.software4you.ulib.ULibVelocityPlugin;
import eu.software4you.ulib.minecraft.proxybridge.ProxyServerBridge;
import eu.software4you.ulib.minecraft.proxybridge.command.Command;
import eu.software4you.ulib.minecraft.proxybridge.message.Message;
import eu.software4you.ulib.minecraft.proxybridge.message.MessageType;
import eu.software4you.velocity.plugin.VelocityPlugin;
import lombok.SneakyThrows;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.Future;

public final class ProxyServerBridgeImpl extends eu.software4you.ulib.impl.minecraft.proxybridge.ProxyServerBridge {
    public static final ChannelIdentifier IDENTIFIER = new LegacyChannelIdentifier(ProxyServerBridge.CHANNEL);
    private final VelocityPlugin plugin;
    private ServerConnection lastReceivedRequest = null;
    private ServerConnection lastReceivedCommand = null;

    private ProxyServerBridgeImpl(VelocityPlugin plugin) {
        this.plugin = plugin;
        registerCommand(new Command("ServerName", (args, origin) -> origin.getBytes(StandardCharsets.UTF_8)));
    }

    public static ProxyServerBridgeImpl init(ULibVelocityPlugin pl) {
        return ImplInjector.inject(new ProxyServerBridgeImpl(pl), ProxyServerBridge.class);
    }

    private void sendMessage(ChannelMessageSink sink, Message message) {
        sink.sendPluginMessage(IDENTIFIER, new Gson().toJson(message).getBytes());
    }

    private RegisteredServer findServer(String serverName) {
        var server = plugin.getProxyServer().getServer(serverName);
        if (!server.isPresent())
            throw new IllegalArgumentException(String.format("Server %s was not found or is not connected", serverName));
        return server.get();
    }

    @Override
    public Future<byte[]> request(String targetServer, String line, final long timeout) {
        RegisteredServer server = findServer(targetServer);
        Message message = new Message(UUID.randomUUID(), null, MessageType.REQUEST, line.getBytes(StandardCharsets.UTF_8));
        sendMessage(server, message);
        return awaitData(message.getId(), timeout);
    }

    @Override
    public Future<byte[]> request(String line, long timeout) {
        if (lastReceivedRequest == null)
            throw new IllegalStateException("No target server known");
        return request(lastReceivedRequest.getServerInfo().getName(), line, timeout);
    }

    @Override
    public void trigger(String targetServer, String line) {
        RegisteredServer server = findServer(targetServer);
        sendMessage(server, new Message(UUID.randomUUID(), null, MessageType.COMMAND, line.getBytes(StandardCharsets.UTF_8)));
    }

    @Override
    public void trigger(String line) {
        if (lastReceivedCommand == null)
            throw new IllegalStateException("No target server known");
        sendMessage(lastReceivedCommand, new Message(UUID.randomUUID(), null, MessageType.COMMAND, line.getBytes(StandardCharsets.UTF_8)));
    }

    @SneakyThrows
    @Subscribe
    public void handle(PluginMessageEvent e) {
        if (!e.getIdentifier().equals(IDENTIFIER))
            return;

        JsonElement je = JsonParser.parseString(new String(e.getData(), StandardCharsets.UTF_8));
        if (!je.isJsonObject())
            return;

        if (!(e.getSource() instanceof ServerConnection from))
            return;

        Message message = new Gson().fromJson(je, Message.class);

        switch (message.getType()) {
            case REQUEST:
                String line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).ifPresent(pc -> {
                    byte[] result = pc.execute(from.getServerInfo().getName());
                    sendMessage(from, new Message(message.getId(), PROXY_SERVER_NAME, MessageType.ANSWER, result));
                    lastReceivedRequest = from;
                });
                break;
            case COMMAND:
                line = new String(message.getData(), StandardCharsets.UTF_8);
                parseCommand(line).ifPresent(pc -> {
                    pc.execute(from.getServerInfo().getName());
                    lastReceivedCommand = from;
                });
                break;
            case ANSWER:
                putData(message.getId(), message.getData());
                break;
        }
    }
}
