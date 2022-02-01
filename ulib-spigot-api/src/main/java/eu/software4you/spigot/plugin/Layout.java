package eu.software4you.spigot.plugin;

import eu.software4you.configuration.yaml.ExtYamlSub;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Spigot variation of {@link eu.software4you.ulib.minecraft.plugin.Layout} with {@link CommandSender} as receiver.
 */
public interface Layout extends eu.software4you.ulib.minecraft.plugin.Layout<CommandSender>, ExtYamlSub {
    @Override
    default void sendMessage(@NotNull CommandSender receiver, String message) {
        receiver.sendMessage(message);
    }

    @Override
    default void sendMessage(@NotNull CommandSender receiver, Iterable<String> messages) {
        if (messages == null) {
            sendMessage(receiver, "null");
            return;
        }
        List<String> li = new ArrayList<>();
        messages.forEach(li::add);
        receiver.sendMessage(li.toArray(new String[0]));
    }

    @Override
    @NotNull
    Layout subAndCreate(@NotNull String path);

    @Override
    Layout getSub(@NotNull String path);

    @Override
    @NotNull
    Layout createSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends Layout> getSubs();
}
