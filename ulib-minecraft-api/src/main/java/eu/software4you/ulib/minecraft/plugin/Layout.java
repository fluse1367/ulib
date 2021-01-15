package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

public abstract class Layout<T> extends ConfigurationWrapper {
    public Layout(ConfigurationSection section) {
        super(section);
    }

    @Deprecated
    public void sendString(T receiver, String path, String... replacements) {
        sendString(receiver, path, null, replacements);
    }

    public void sendString(T receiver, String path, Object def, String... replacements) {
        sendMessage(receiver, string(path, def, replacements));
    }

    @Deprecated
    public void sendList(T receiver, String path, String... replacements) {
        sendList(receiver, path, new ArrayList<>(), replacements);
    }

    public void sendList(T receiver, String path, List<String> def, String... replacements) {
        sendMessage(receiver, stringList(path, def, replacements).toArray(new String[0]));
    }

    @Deprecated
    public void send(T receiver, String path) {
        send(receiver, path, null);
    }

    public void send(T receiver, String path, Object def) {
        sendMessage(receiver, String.valueOf(get(Object.class, path, def)));
    }

    @Override
    public Layout<T> sub(String s) {
        return section().isConfigurationSection(s) ? create(section(s)) : null;
    }

    @Override
    public Layout<T> subAndCreate(String s) {
        return create(sectionAndCreate(s));
    }

    protected void sendMessage(T receiver, String[] messages) {
        for (String message : messages) {
            sendMessage(receiver, message);
        }
    }

    protected abstract Layout<T> create(ConfigurationSection section);

    protected abstract void sendMessage(T receiver, String message);
}
