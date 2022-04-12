package eu.software4you.ulib.minecraft.launchermeta;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * Represents a library-artifact.
 */
public interface RemoteArtifact extends RemoteResource {
    /**
     * Returns the artifact's path.
     *
     * @return the path
     */
    @NotNull
    Path getPath();
}
