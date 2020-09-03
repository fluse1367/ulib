package eu.software4you.proxy.configuration;

import eu.software4you.configuration.ConfigurationWrapper;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class LayoutWrapper extends ConfigurationWrapper {
    public LayoutWrapper(ConfigurationSection section) {
        super(section);
    }

    @Deprecated
    public void sendString(CommandSender receiver, String path, String... replacements) {
        sendString(receiver, path, null, replacements);
    }

    public void sendString(CommandSender receiver, String path, Object def, String... replacements) {
        receiver.sendMessage(new TextComponent(string(path, def, replacements)));
    }

    @Deprecated
    public void sendList(CommandSender receiver, String path, String... replacements) {
        sendList(receiver, path, new ArrayList<>(), replacements);
    }

    public void sendList(CommandSender receiver, String path, List<String> def, String... replacements) {
        for (String s : stringList(path, def, replacements).toArray(new String[0])) {
            receiver.sendMessage(new TextComponent(s));
        }
    }

    @Deprecated
    public void send(CommandSender receiver, String path) {
        send(receiver, path, null);
    }

    public void send(CommandSender receiver, String path, Object def) {
        receiver.sendMessage(new TextComponent(String.valueOf(get(Object.class, path, def))));
    }
}
