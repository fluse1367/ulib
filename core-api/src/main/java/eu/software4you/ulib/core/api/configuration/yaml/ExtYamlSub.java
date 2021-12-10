package eu.software4you.ulib.core.api.configuration.yaml;

import eu.software4you.ulib.core.api.configuration.ExtSub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * A {@link YamlSub} with extended functionality and shortcuts.
 */
public interface ExtYamlSub extends YamlSub, ExtSub {
    @Override
    @Nullable ExtYamlSub getSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends ExtYamlSub> getSubs();

    @Override
    @NotNull ExtYamlSub createSub(@NotNull String path);

    @Override
    @NotNull ExtYamlSub subAndCreate(@NotNull String path);
}
