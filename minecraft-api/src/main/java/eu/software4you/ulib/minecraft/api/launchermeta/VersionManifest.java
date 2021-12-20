package eu.software4you.ulib.minecraft.api.launchermeta;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.Map;

/**
 * Limited representation of a minecraft version manifest.
 */
public interface VersionManifest {
    /**
     * Returns the id.
     *
     * @return the id
     */
    @NotNull
    String getId();

    /**
     * Returns the asset index.
     *
     * @return the asset index
     */
    @NotNull
    RemoteResource getAssetIndex();

    /**
     * Returns the version type
     *
     * @return the version type
     */
    @NotNull
    Type getType();

    /**
     * Returns the representing manifest file url
     *
     * @return the representing manifest file url
     */
    @NotNull
    URL getUrl();

    /**
     * Returns the time
     *
     * @return the time
     */
    @NotNull
    OffsetDateTime getTime();

    /**
     * Returns the release time
     *
     * @return the release time
     */
    @NotNull
    OffsetDateTime getReleaseTime();

    /**
     * Returns a download object.
     *
     * @param id the artifact id
     * @return the download object, or {@code null} if not found
     */
    @Nullable
    RemoteResource getDownload(@NotNull String id);

    /**
     * Returns all downloads of the manifest.
     *
     * @return all the downloads with their id as key
     */
    @NotNull
    Map<String, RemoteResource> getDownloads();

    /**
     * Returns the libraries.
     *
     * @return the libraries
     */
    @NotNull
    Collection<RemoteLibrary> getLibraries();

    /**
     * Representation of different version types.
     */
    enum Type {
        RELEASE,
        SNAPSHOT,
        OLD_BETA
    }
}
