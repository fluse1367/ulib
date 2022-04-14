package eu.software4you.ulib.spigot.mappings;

import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import eu.software4you.ulib.spigot.impl.mappings.MappingsImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Util to read mappings.
 *
 * @see VersionsMeta#getCurrent()
 * @see VersionsMeta#get(String)
 */
public final class Mappings {

    /**
     * Loads server vanilla mappings from a specific manifest.
     * <p>
     * <b>Warning:</b> Loading mappings is resource intense and may take some time.
     *
     * @param manifest the source manifest
     * @return the mapping, or {@code null} if the manifest does not contain mappings
     * @see VersionsMeta#getCurrent()
     * @see VersionsMeta#get(String)
     */
    @NotNull
    public static Optional<VanillaMapping> loadVanillaServerMapping(@NotNull VersionManifest manifest) {
        return Optional.ofNullable(MappingsImpl.loadVanilla(manifest));
    }

    /**
     * Returns the server vanilla mappings for the current minecraft version.
     * <p>
     * Returns a cached version if possible, otherwise loads it fist.
     * <p>
     * <b>Warning:</b> Loading mappings is resource intense and may take some time.
     *
     * @return the mapping
     */
    @NotNull
    public static VanillaMapping getVanillaMapping() {
        return MappingsImpl.getCurrentVanilla();
    }

    /**
     * Loads Bukkit mappings for a specific version.
     * <p>
     * <b>Warning:</b> Loading mappings is resource intense and may take some time.
     *
     * @param version the plain mc version string (e.g. {@code 1.16.5})
     * @return the mapping, or {@code null} if a mapping could not be found for that version
     */
    @NotNull
    public static Optional<BukkitMapping> loadBukkitMapping(@NotNull String version) {
        return Optional.ofNullable(MappingsImpl.loadBukkit(version));
    }

    /**
     * Returns the Bukkit mappings for the current minecraft version.
     * <p>
     * Returns a cached version if possible, otherwise loads it fist.
     * <p>
     * <b>Warning:</b> Loading mappings is resource intense and may take some time.
     *
     * @return the mapping
     */
    @NotNull
    public static BukkitMapping getBukkitMapping() {
        return MappingsImpl.getCurrentBukkit();
    }

    /**
     * Loads mixed mappings for a specific version.
     * <p>
     * <b>Warning:</b> Loading mixed mappings is very resource intense and may take some time.
     *
     * @param manifest source manifest
     * @return the mapping, or {@code null} if a mapping could not be found for that version
     */
    @NotNull
    public static Optional<MixedMapping> loadMixedMapping(@NotNull VersionManifest manifest) {
        return Optional.ofNullable(MappingsImpl.loadMixed(manifest));
    }

    /**
     * Returns the mixed mappings for the current minecraft version.
     * <p>
     * Returns a cached version if possible, otherwise loads it fist.
     * <p>
     * <b>Warning:</b> Loading mixed mappings is very resource intense and may take some time.
     *
     * @return the mapping
     */
    @NotNull
    public static MixedMapping getMixedMapping() {
        return MappingsImpl.getCurrentMixed();
    }
}
