package eu.software4you.spigot.plugin;

import eu.software4you.ulib.minecraft.plugin.Layout;
import org.bukkit.command.CommandSender;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

public class SpigotLayout extends Layout<CommandSender> {
    public SpigotLayout(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected Layout<CommandSender> create(ConfigurationSection section) {
        return new SpigotLayout(section);
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
