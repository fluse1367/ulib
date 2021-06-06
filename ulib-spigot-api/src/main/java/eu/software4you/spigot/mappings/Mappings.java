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
     * <p>
     * <b>Warning:</b> Loading mappings is resource intense and may take some time.
     *
     * @param manifest the source manifest
     * @return the mapping, or {@code null} if the manifest does not contain mappings
     */
    @Nullable
    public static VanillaMapping loadVanillaServerMapping(@NotNull VersionManifest manifest) {
        return impl.loadVanilla(manifest.getDownload("server_mappings"));
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
        return impl.getCurrentVanilla();
    }

    /**
     * Loads Bukkit mappings for a specific version.
     * <p>
     * <b>Warning:</b> Loading mappings is resource intense and may take some time.
     *
     * @param version the plain mc version string (e.g. {@code 1.16.5})
     * @return the mapping, or {@code null} if a mapping could not be found for that version
     */
    @Nullable
    public static BukkitMapping loadBukkitMapping(@NotNull String version) {
        return impl.loadBukkit(version);
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
        return impl.getCurrentBukkit();
    }

    protected abstract VanillaMapping loadVanilla(RemoteResource mapping);

    protected abstract VanillaMapping getCurrentVanilla();

    protected abstract BukkitMapping loadBukkit(String version);

    protected abstract BukkitMapping getCurrentBukkit();
}
