package eu.software4you.ulib.bungeecord.api.plugin;

import eu.software4you.ulib.core.api.configuration.yaml.YamlSub;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * BungeeCord variant of {@link eu.software4you.ulib.minecraft.api.plugin.Layout} with {@link CommandSender} as receiver.
 */
public interface Layout extends eu.software4you.ulib.minecraft.api.plugin.Layout<CommandSender>, YamlSub {
    @Override
    default void sendMessage(@NotNull CommandSender receiver, String message) {
        receiver.sendMessage(new TextComponent(message));
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
