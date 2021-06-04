package eu.software4you.spigot.mappings;

import eu.software4you.ulib.Await;
import eu.software4you.ulib.minecraft.launchermeta.LauncherMeta;
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
     * Loads the client mappings.
     *
     * @param manifest the source manifest
     * @return the mappings, or {@code null} if the manifest does not contain mappings
     */
    @Nullable
    public static JarMapping loadClientMappings(@NotNull VersionManifest manifest) {
        return impl.load(manifest, "client_mappings");
    }

    /**
     * Loads the server mappings.
     *
     * @param manifest the source manifest
     * @return the mappings, or {@code null} if the manifest does not contain mappings
     */
    @Nullable
    public static JarMapping loadServerMappings(@NotNull VersionManifest manifest) {
        return impl.load(manifest, "server_mappings");
    }

    protected abstract JarMapping load(VersionManifest manifest, String what);

}
