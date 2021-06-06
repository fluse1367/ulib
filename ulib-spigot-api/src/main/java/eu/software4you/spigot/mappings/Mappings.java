package eu.software4you.spigot.mappings;

import eu.software4you.ulib.Await;
import eu.software4you.ulib.minecraft.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.launchermeta.RemoteResource;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Util to read mappings.
 *
 * @see LauncherMeta#getVersionManifest()
 * @see VersionsMeta#get(String)
 */
public abstract class Mappings {
    @Await
    private static Mappings impl;

    /**
     * Loads server vanilla mappings from a specific manifest.
     *
     * @param manifest the source manifest
     * @return the mapping, or {@code null} if the manifest does not contain mappings
     */
    @Nullable
    public static VanillaMapping loadVanillaServerMappings(@NotNull VersionManifest manifest) {
        return impl.loadVanilla(manifest.getDownload("server_mappings"));
    }

    /**
     * Returns the server vanilla mappings for the current minecraft version.
     * <p>
     * Returns a cached version if possible, otherwise loads it fist.
     *
     * @return the mapping
     */
    @NotNull
    public static VanillaMapping getVanillaMapping() {
        return impl.getCurrentVanilla();
    }

    protected abstract VanillaMapping loadVanilla(RemoteResource mapping);

    protected abstract VanillaMapping getCurrentVanilla();

}
