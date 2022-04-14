package eu.software4you.ulib.minecraft.launchermeta;

import eu.software4you.ulib.minecraft.impl.launchermeta.Meta;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Optional;

/**
 * Representation of <a href="https://launchermeta.mojang.com/mc/game/version_manifest.json" target="_blank">version_manifest.json</a>.
 */
public interface VersionsMeta {

    /**
     * Returns a representation instance of the {@code version_manifest.json} file.
     * <p>
     * Returns a cached version if possible, otherwise downloads the file first.
     *
     * @return the version metadata
     */
    @NotNull
    static VersionsMeta getCurrent() {
        return Meta.INSTANCE.get();
    }

    /**
     * Clears the version manifest. The next call to {@link #getCurrent()} will re-download it.
     *
     * @throws IllegalStateException when attempting to unload while the manifest is being loaded
     */
    static void clearCurrent() {
        if (Meta.INSTANCE.isRunning())
            throw new IllegalStateException("Cannot clear while loading");
        Meta.INSTANCE.reset();
    }

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
    @NotNull
    Optional<VersionManifest> get(@NotNull String id);

    /**
     * Returns all loaded versions
     *
     * @return all loaded versions
     */
    @NotNull
    Collection<VersionManifest> getVersions();
}
