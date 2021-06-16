package eu.software4you.ulib.impl.minecraft.plugin;

import eu.software4you.ulib.impl.configuration.yaml.ExtYamlDocument;
import eu.software4you.ulib.impl.configuration.yaml.YamlDocument;
import eu.software4you.ulib.impl.configuration.yaml.YamlSerializer;
import eu.software4you.ulib.minecraft.plugin.Layout;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collection;

public abstract class YamlLayout<T> extends ExtYamlDocument implements Layout<T> {
    // constructor for empty root
    protected YamlLayout() {
        super(YamlSerializer.getInstance());
    }

    // constructor for sub
    protected YamlLayout(YamlDocument parent, String key, Node valueNode) {
        super(parent, key, valueNode);
    }

    @Override
    protected abstract YamlLayout<T> constructChild(String key, Node valueNode);

    @Override
    public @NotNull YamlLayout<T> subAndCreate(@NotNull String path) {
        return (YamlLayout<T>) super.subAndCreate(path);
    }

    @Override
    public @Nullable YamlLayout<T> getSub(@NotNull String path) {
        return (YamlLayout<T>) super.getSub(path);
    }

    @Override
    public @NotNull YamlLayout<T> createSub(@NotNull String fullPath) {
        return (YamlLayout<T>) super.createSub(fullPath);
    }

    @Override
    public @NotNull Collection<? extends YamlLayout<T>> getSubs() {
        return (Collection<? extends YamlLayout<T>>) super.getSubs();
    }
}
