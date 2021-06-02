package eu.software4you.spigot.plugin;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Spigot implementation of {@link eu.software4you.ulib.minecraft.plugin.Layout} with {@link CommandSender} as receiver.
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
        receiver.sendMessage(message);
    }

    @Override
    protected void sendMessage(@NotNull CommandSender receiver, Iterable<String> messages) {
        if (messages == null) {
            super.sendMessage(receiver, (Iterable<String>) null);
            return;
        }
        List<String> li = new ArrayList<>();
        messages.forEach(li::add);
        receiver.sendMessage(li.toArray(new String[0]));
    }
}
