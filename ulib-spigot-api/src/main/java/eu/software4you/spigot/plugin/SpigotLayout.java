package eu.software4you.spigot.plugin;

import eu.software4you.ulib.minecraft.plugin.Layout;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;

/**
 * Spigot implementation of {@link Layout} with {@link CommandSender} as receiver.
 */
public class SpigotLayout extends Layout<CommandSender> {
    public SpigotLayout(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected SpigotLayout create(ConfigurationSection section) {
        return new SpigotLayout(section);
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
