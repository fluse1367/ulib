package eu.software4you.velocity.plugin;

import eu.software4you.ulib.minecraft.plugin.Layout;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import ulib.ported.org.bukkit.configuration.ConfigurationSection;

public class VelocityLayout extends Layout<Audience> {
    VelocityLayout(ConfigurationSection section) {
        super(section);
    }

    @Override
    protected Layout<Audience> create(ConfigurationSection section) {
        return new VelocityLayout(section);
    }

    @Override
    protected void sendMessage(Audience receiver, String message) {
        receiver.sendMessage(Component.text(message));
    }
}
