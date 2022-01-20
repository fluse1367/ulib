package eu.software4you.ulib.velocity.impl.plugin;

import eu.software4you.ulib.core.impl.configuration.yaml.YamlDocument;
import eu.software4you.ulib.minecraft.impl.plugin.YamlLayout;
import eu.software4you.ulib.velocity.api.internal.Providers;
import eu.software4you.ulib.velocity.api.plugin.Layout;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;

import java.util.Collection;

public final class LayoutImpl extends YamlLayout<Audience> implements Layout {

    LayoutImpl() {
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

    public static final class LayoutProvider implements Providers.ProviderLayout {
        @Override
        public Class<? extends Layout> type() {
            return LayoutImpl.class;
        }

        @Override
        public Layout get() {
            return new LayoutImpl();
        }
    }

}
