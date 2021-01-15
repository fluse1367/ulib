package eu.software4you.spigot.plugin;

import eu.software4you.ulib.minecraft.plugin.Layout;
import org.bukkit.command.CommandSender;
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
        receiver.sendMessage(message);
    }

    @Override
    protected void sendMessage(CommandSender receiver, String[] messages) {
        receiver.sendMessage(messages);
    }
}
