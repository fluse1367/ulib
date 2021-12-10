package eu.software4you.ulib.minecraft.plugin;

import eu.software4you.ulib.core.api.configuration.ExtSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

/**
 * An variation of the {@link ExtSub} to provide direct sending of e.g. messages to players.
 *
 * @param <T> the message receiver type
 */
public interface Layout<T> extends ExtSub {

    /**
     * Reads a String, processes it with {@link String#format(String, Object...)}
     * if {@code replacements} are given, and sends it.
     *
     * @param receiver     the receiver
     * @param key          the key
     * @param replacements the replacements
     */
    default void sendString(@NotNull T receiver, @NotNull String key, Object... replacements) {
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
    default void sendString(@NotNull T receiver, @NotNull String key, @Nullable String def, Object... replacements) {
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
    default void sendList(@NotNull T receiver, @NotNull String key, Object... replacements) {
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
    default void sendList(@NotNull T receiver, @NotNull String key, @Nullable List<String> def, Object... replacements) {
        sendMessage(receiver, stringList(key, def, replacements));
    }

    /**
     * Reads a value, converts to a String, and sends it.
     *
     * @param receiver the receiver
     * @param key      the key
     */
    default void send(@NotNull T receiver, @NotNull String key) {
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
    default void send(@NotNull T receiver, @NotNull String key, @Nullable Object def) {
        sendMessage(receiver, String.valueOf(get(Object.class, key, def)));
    }

    /**
     * Sends multiple messages, or {@code "null"} if {@code messages} equal {@code null}.
     *
     * @param receiver the receiver of the message
     * @param messages the messages
     */
    default void sendMessage(@NotNull T receiver, @Nullable Iterable<String> messages) {
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
    void sendMessage(@NotNull T receiver, @Nullable String message);

    @Override
    @NotNull Layout<T> subAndCreate(@NotNull String path);

    @Override
    @Nullable Layout<T> getSub(@NotNull String path);

    @Override
    @NotNull Layout<T> createSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends Layout<T>> getSubs();
}
