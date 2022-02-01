package eu.software4you.ulib.minecraft.launchermeta;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * Represents a library-sub from a version manifest.
 */
public interface RemoteLibrary {
    /**
     * Returns the artifacts.
     *
     * @return a collection containing the downloadable artifacts with their id as key
     */
    @NotNull
    Map<String, RemoteArtifact> getDownloads();

    /**
     * Returns the corresponding maven coordinates.
     *
     * @return maven coordinates
     */
    @NotNull
    String getName();
}
