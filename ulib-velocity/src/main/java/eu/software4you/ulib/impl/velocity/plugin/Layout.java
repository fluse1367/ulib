package eu.software4you.ulib.impl.velocity.plugin;

import eu.software4you.ulib.impl.configuration.yaml.YamlDocument;
import eu.software4you.ulib.impl.minecraft.plugin.YamlLayout;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collection;

public class Layout extends YamlLayout<Audience> implements eu.software4you.velocity.plugin.Layout {
    Layout() {
        // empty root
    }

    private Layout(YamlDocument parent, String key, Node valueNode) {
        super(parent, key, valueNode);
    }

    @Override
    protected Layout constructChild(String key, Node valueNode) {
        return new Layout(this, key, valueNode);
    }

    @Override
    public @NotNull Layout subAndCreate(@NotNull String path) {
        return (Layout) super.subAndCreate(path);
    }

    @Override
    public @Nullable Layout getSub(@NotNull String path) {
        return (Layout) super.getSub(path);
    }

    @Override
    public @NotNull Layout createSub(@NotNull String fullPath) {
        return (Layout) super.createSub(fullPath);
    }

    @Override
    public @NotNull Collection<? extends Layout> getSubs() {
        return (Collection<? extends Layout>) super.getSubs();
    }
}