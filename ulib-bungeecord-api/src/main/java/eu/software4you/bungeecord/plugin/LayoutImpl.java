package eu.software4you.bungeecord.plugin;

import eu.software4you.ulib.minecraft.plugin.Layout;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

class LayoutImpl extends Layout<CommandSender> {
    LayoutImpl(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected Layout<CommandSender> create(ConfigurationSection section) {
        return new LayoutImpl(section);
    }

    @Override
    protected void sendMessage(CommandSender receiver, String message) {
        receiver.sendMessage(new TextComponent(message));
    }
}
