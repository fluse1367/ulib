package eu.software4you.ulib.minecraft.api.launchermeta;

import eu.software4you.ulib.core.api.internal.Providers;
import org.jetbrains.annotations.NotNull;

/**
 * Access point for launcher metadata.
 */
public abstract class LauncherMeta {

    private static LauncherMeta getInstance() {
        return Providers.get(LauncherMeta.class);
    }

    /**
     * Returns the {@code version_manifest.json} file.
     * <p>
     * Returns a cached version if possible, otherwise downloads the file first.
     *
     * @return the version metadata
     */
    @NotNull
    public static VersionsMeta getVersionManifest() {
        return getInstance().getVersionManifest0();
    }

    /**
     * Clears the version manifest. The next call to {@link #getVersionManifest()} will re-download it.
     *
     * @throws IllegalStateException when attempting to unload while the manifest is being loaded
     */
    public static void unloadVersionManifest() {
        getInstance().reset0();
    }

    protected abstract VersionsMeta getVersionManifest0();

    protected abstract void reset0();
}
