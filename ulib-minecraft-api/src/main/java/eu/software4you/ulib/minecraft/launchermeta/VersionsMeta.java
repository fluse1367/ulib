package eu.software4you.ulib.minecraft.launchermeta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Representation of <a href="https://launchermeta.mojang.com/mc/game/version_manifest.json" target="_blank">version_manifest.json</a>.
 */
public interface VersionsMeta {

    /**
     * Returns the current release manifest
     *
     * @return the current release manifest
     */
    @NotNull
    VersionManifest getRelease();

    /**
     * Returns the current snapshot manifest
     *
     * @return the current snapshot manifest
     */
    @NotNull
    VersionManifest getSnapshot();

    /**
     * Returns a specific manifest.
     * <p>
     * Returns a cached version if possible, otherwise loads it first.
     *
     * @param id the version id
     * @return the loaded manifest, or {@code null} if not found
     */
    @Nullable
    VersionManifest get(@NotNull String id);

    /**
     * Returns all loaded versions
     *
     * @return all loaded versions
     */
    @NotNull
    Collection<VersionManifest> getVersions();
}
