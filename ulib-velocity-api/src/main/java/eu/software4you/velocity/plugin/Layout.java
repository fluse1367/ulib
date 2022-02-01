package eu.software4you.velocity.plugin;

import eu.software4you.configuration.yaml.ExtYamlSub;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Velocity variation of {@link eu.software4you.ulib.minecraft.plugin.Layout} with {@link Audience} as receiver.
 */
public interface Layout extends eu.software4you.ulib.minecraft.plugin.Layout<Audience>, ExtYamlSub {

    @Override
    default void sendMessage(@NotNull Audience receiver, String message) {
        receiver.sendMessage(Component.text(message));
    }

    @Override
    @NotNull
    Layout subAndCreate(@NotNull String path);

    @Override
    @Nullable
    Layout getSub(@NotNull String path);

    @Override
    @NotNull
    Layout createSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends Layout> getSubs();
}
