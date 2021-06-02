package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.configuration.ConfigurationWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.List;

/**
 * An extended version of the config wrapper to provide direct sending of e.g. messages to players.
 *
 * @param <T> the message receiver type
 * @see eu.software4you.configuration.ConfigurationWrapper
 */
public abstract class Layout<T> extends ConfigurationWrapper {
    /**
     * The default constructor.
     *
     * @param section the configuration section to wrap
     */
    public Layout(@Nullable ConfigurationSection section) {
        super(section);
    }

    /**
     * Reads a String, processes it with {@link String#format(String, Object...)}
     * if {@code replacements} are given, and sends it.
     *
     * @param receiver     the receiver
     * @param key          the key
     * @param replacements the replacements
     */
    public void sendString(@NotNull T receiver, @NotNull String key, Object... replacements) {
        sendMessage(receiver, string(key, replacements));
    }

    /**
     * Reads a String, processes it with {@link String#format(String, Object...)}
     * if {@code replacements} are given, and sends it.
     * If {@code key} does not exist, {@code def} will be sent instead (without processing).
     *
     * @param receiver     the receiver
     * @param key          the key
     * @param def          default value that will be sent if {@code key} does not exist
     * @param replacements the replacements
     */
    public void sendString(@NotNull T receiver, @NotNull String key, @Nullable String def, Object... replacements) {
        sendMessage(receiver, string(key, def, replacements));
    }

    /**
     * Reads a String type List, processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given, and sends it.
     *
     * @param receiver     the receiver
     * @param key          the key
     * @param replacements the replacements
     */
    public void sendList(@NotNull T receiver, @NotNull String key, Object... replacements) {
        sendMessage(receiver, stringList(key, replacements));
    }

    /**
     * Reads a String type List, processes each entry with {@link String#format(String, Object...)}
     * if {@code replacements} are given, and sends it.
     * If {@code key} does not exist, {@code def} will be sent instead (without processing).
     *
     * @param receiver     the receiver
     * @param key          the key
     * @param def          default value that will be sent if {@code key} does not exist
     * @param replacements the replacements
     */
    public void sendList(@NotNull T receiver, @NotNull String key, @Nullable List<String> def, Object... replacements) {
        sendMessage(receiver, stringList(key, def, replacements));
    }

    /**
     * Reads a value, converts to a String, and sends it.
     *
     * @param receiver the receiver
     * @param key      the key
     */
    public void send(@NotNull T receiver, @NotNull String key) {
        sendMessage(receiver, String.valueOf(get(Object.class, key)));
    }

    /**
     * Reads a value, converts to a String, and sends it.
     * If {@code key} does not exist, {@code def} will be sent instead.
     *
     * @param receiver the receiver
     * @param key      the key
     * @param def      default value that will be sent if {@code key} does not exist
     */
    public void send(@NotNull T receiver, @NotNull String key, @Nullable Object def) {
        sendMessage(receiver, String.valueOf(get(Object.class, key, def)));
    }

    @Override
    @Nullable
    public Layout<T> sub(@NotNull String key) {
        return section().isConfigurationSection(key) ? create(section(key)) : null;
    }

    @Override
    @NotNull
    public Layout<T> subAndCreate(@NotNull String key) {
        return create(sectionAndCreate(key));
    }

    /**
     * Creates a new instance of the layout.
     *
     * @param section the section
     * @return the new instance
     */
    protected abstract Layout<T> create(@Nullable ConfigurationSection section);

    /**
     * Sends multiple messages, or {@code "null"} if {@code messages} equal {@code null}.
     *
     * @param receiver the receiver of the message
     * @param messages the messages
     */
    protected void sendMessage(@NotNull T receiver, @Nullable Iterable<String> messages) {
        if (messages == null) {
            sendMessage(receiver, "null");
            return;
        }
        for (String message : messages) {
            sendMessage(receiver, message);
        }
    }

    /**
     * Sends a message.
     *
     * @param receiver the receiver of the message
     * @param message  the message to send
     */
    protected abstract void sendMessage(@NotNull T receiver, @Nullable String message);
}
