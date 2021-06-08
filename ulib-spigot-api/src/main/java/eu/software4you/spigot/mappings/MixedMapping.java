package eu.software4you.spigot.mappings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a mixture of a {@link VanillaMapping Vanilla-} and {@link BukkitMapping Bukkit-}Mapping.
 * <p>
 * The classes have the Bukkit names as source and the Vanilla source names as mappings (Bukkit -> Vanilla Source).<br>
 * The method and fields have the Vanilla source names as source and the Bukkit names as mapping (Vanilla Source -> Bukkit).
 * <p>
 * This mapping is useful for deep dynamic reflection.
 * <p>
 * <b>Note:</b> This mapping does not contain any Vanilla (jar) mapped values.
 *
 * @see VanillaMapping
 * @see BukkitMapping
 */
public interface MixedMapping extends JarMapping {

    /**
     * Returns a specific class mapping.
     *
     * @param source the source (Bukkit) class
     * @return the mapping, or {@code null} if not found
     */
    @Nullable
    ClassMapping from(@NotNull Class<?> source);
}