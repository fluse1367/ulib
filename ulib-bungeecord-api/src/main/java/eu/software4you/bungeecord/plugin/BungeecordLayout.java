package eu.software4you.bungeecord.plugin;

import eu.software4you.ulib.minecraft.plugin.Layout;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import org.jetbrains.annotations.NotNull;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

public class BungeecordLayout extends Layout<CommandSender> {
    public BungeecordLayout(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected Layout<CommandSender> create(ConfigurationSection section) {
        return new BungeecordLayout(section);
    }

    @Override
    protected void sendMessage(@NotNull CommandSender receiver, String message) {
        receiver.sendMessage(new TextComponent(message));
    }
}
