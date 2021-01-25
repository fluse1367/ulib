package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.List;

public abstract class Layout<T> extends ConfigurationWrapper {
    public Layout(ConfigurationSection section) {
        super(section);
    }

    public void sendString(T receiver, String path, Object def, Object... replacements) {
        sendMessage(receiver, string(path, def, replacements));
    }

    public void sendList(T receiver, String path, List<String> def, Object... replacements) {
        sendMessage(receiver, stringList(path, def, replacements).toArray(new String[0]));
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
