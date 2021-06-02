package eu.software4you.velocity.plugin;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

/**
 * Velocity implementation of {@link eu.software4you.ulib.minecraft.plugin.Layout} with {@link Audience} as receiver.
 */
public class Layout extends eu.software4you.ulib.minecraft.plugin.Layout<Audience> {
    Layout(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected Layout create(ConfigurationSection section) {
        return new Layout(section);
    }

    @Override
    protected void sendMessage(@NotNull Audience receiver, String message) {
        receiver.sendMessage(Component.text(message));
    }
}
