package eu.software4you.ulib.impl.velocity.plugin;

import eu.software4you.ulib.core.impl.configuration.yaml.YamlDocument;
import eu.software4you.ulib.impl.minecraft.plugin.YamlLayout;
import eu.software4you.ulib.inject.Factory;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.velocity.plugin.VelocityJavaPlugin;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collection;

@Impl(VelocityJavaPlugin.class)
final class LayoutImpl extends YamlLayout<Audience> implements eu.software4you.velocity.plugin.Layout {

    @Factory
    private LayoutImpl() {
        // empty root
    }

    private LayoutImpl(YamlDocument parent, String key, Node valueNode) {
        super(parent, key, valueNode);
    }

    @Override
    protected LayoutImpl constructChild(String key, Node valueNode) {
        return new LayoutImpl(this, key, valueNode);
    }

    @Override
    public @NotNull LayoutImpl subAndCreate(@NotNull String path) {
        return (LayoutImpl) super.subAndCreate(path);
    }

    @Override
    public @Nullable LayoutImpl getSub(@NotNull String path) {
        return (LayoutImpl) super.getSub(path);
    }

    @Override
    public @NotNull LayoutImpl createSub(@NotNull String fullPath) {
        return (LayoutImpl) super.createSub(fullPath);
    }

    @Override
    public @NotNull Collection<? extends LayoutImpl> getSubs() {
        return (Collection<? extends LayoutImpl>) super.getSubs();
    }
}
