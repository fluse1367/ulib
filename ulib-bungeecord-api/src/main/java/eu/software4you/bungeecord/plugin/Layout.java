package eu.software4you.bungeecord.plugin;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

/**
 * BungeeCord implementation of {@link eu.software4you.ulib.minecraft.plugin.Layout} with {@link CommandSender} as receiver.
 */
public class Layout extends eu.software4you.ulib.minecraft.plugin.Layout<CommandSender> {
    public Layout(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected Layout create(ConfigurationSection section) {
        return new Layout(section);
    }

    @Override
    protected void sendMessage(@NotNull CommandSender receiver, String message) {
        receiver.sendMessage(new TextComponent(message));
    }
}
