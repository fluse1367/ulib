package eu.software4you.ulib.minecraft.impl.launchermeta;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.minecraft.launchermeta.RemoteArtifact;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.nio.file.Paths;

final class Artifact extends Resource implements RemoteArtifact {
    private final Path path;

    Artifact(String id, String path, JsonConfiguration json) {
        super(id, json);
        this.path = Paths.get(path);
    }

    @Override
    public @NotNull Path getPath() {
        return path;
    }
}
