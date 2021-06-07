package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Mapping for a name-mapped jar.
 */
public interface JarMapping {

    /**
     * Returns all class mappings.
     *
     * @return all class mappings
     */
    @NotNull
    Collection<ClassMapping> all();

    /**
     * Returns a specific class mapping.
     *
     * @param sourceName the <b>source</b> (original) fully qualified class name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ClassMapping fromSource(@NotNull String sourceName);

    /**
     * Returns a specific class mapping.
     *
     * @param mappedName the <b>mapped</b> fully qualified class name
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ClassMapping fromMapped(@NotNull String mappedName);
}
