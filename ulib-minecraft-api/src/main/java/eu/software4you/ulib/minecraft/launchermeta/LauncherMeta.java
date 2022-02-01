package eu.software4you.ulib.minecraft.launchermeta;

import eu.software4you.ulib.Await;
import org.jetbrains.annotations.NotNull;

/**
 * Access point for launcher metadata.
 */
public abstract class LauncherMeta {
    @Await
    private static LauncherMeta impl;

    /**
     * Returns the {@code version_manifest.json} file.
     * <p>
     * Returns a cached version if possible, otherwise downloads the file first.
     *
     * @return the version metadata
     */
    @NotNull
    public static VersionsMeta getVersionManifest() {
        return impl.getVersionManifest0();
    }

    /**
     * Clears the version manifest. The next call to {@link #getVersionManifest()} will re-download it.
     *
     * @throws IllegalStateException when attempting to unload while the manifest is being loaded
     */
    public static void unloadVersionManifest() {
        impl.reset0();
    }

    protected abstract VersionsMeta getVersionManifest0();

    protected abstract void reset0();
}
