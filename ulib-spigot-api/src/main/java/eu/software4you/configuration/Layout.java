package eu.software4you.configuration;

import org.bukkit.command.CommandSender;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public class Layout extends ConfigurationWrapper {
    public Layout(ConfigurationSection section) {
        super(section);
    }

    @Deprecated
    public void sendString(CommandSender receiver, String path, String... replacements) {
        sendString(receiver, path, null, replacements);
    }

    public void sendString(CommandSender receiver, String path, Object def, String... replacements) {
        receiver.sendMessage(string(path, def, replacements));
    }

    @Deprecated
    public void sendList(CommandSender receiver, String path, String... replacements) {
        sendList(receiver, path, new ArrayList<>(), replacements);
    }

    public void sendList(CommandSender receiver, String path, List<String> def, String... replacements) {
        receiver.sendMessage(stringList(path, def, replacements).toArray(new String[0]));
    }

    @Deprecated
    public void send(CommandSender receiver, String path) {
        send(receiver, path, null);
    }

    public void send(CommandSender receiver, String path, Object def) {
        receiver.sendMessage(String.valueOf(get(Object.class, path, def)));
    }

    @Override
    public Layout sub(String s) {
        return section().isConfigurationSection(s) ? new Layout(section(s)) : null;
    }

    @Override
    public Layout subAndCreate(String s) {
        return new Layout(sectionAndCreate(s));
    }
}
