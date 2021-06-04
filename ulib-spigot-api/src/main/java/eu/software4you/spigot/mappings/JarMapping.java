package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Mapping for a name-obfuscated jar.
 */
public interface JarMapping {

    /**
     * Returns all class mappings.
     *
     * @return all class mappings
     */
    @NotNull
    Collection<ClassMapping> getAll();

    /**
     * Returns a specific class mapping.
     *
     * @param originalName the <b>un-obfuscated</b> fully qualified class name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ClassMapping get(@NotNull String originalName);

    /**
     * Returns a specific class mapping.
     *
     * @param obfuscatedName the <b>obfuscated</b> fully qualified class name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ClassMapping search(@NotNull String obfuscatedName);
}
