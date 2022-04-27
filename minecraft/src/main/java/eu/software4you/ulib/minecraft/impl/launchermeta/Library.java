package eu.software4you.ulib.minecraft.impl.launchermeta;

import eu.software4you.ulib.core.common.Keyable;
import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.minecraft.launchermeta.RemoteArtifact;
import eu.software4you.ulib.minecraft.launchermeta.RemoteLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.*;

final class Library implements RemoteLibrary {
    private final String mavenCoords;
    private final Map<String, RemoteArtifact> downloads;

    Library(String mavenCoords, JsonConfiguration json /*downloads*/) {
        this.mavenCoords = mavenCoords;

        Map<String, Artifact> downloads = new HashMap<>();
        var arti = json.getSub("artifact").orElseThrow();
        downloads.put("artifact", new Artifact("artifact", arti.string("path").orElseThrow(), arti));

        json.getSub("classifiers")
                .map(s -> s.getSubs(false))
                .ifPresent(c -> c.forEach(sub -> {
                    @SuppressWarnings("unchecked")
                    String id = ((Keyable<String>) sub).getKey();
                    downloads.put(id, new Artifact(id, sub.string("path").orElseThrow(), sub));
                }));

        this.downloads = Collections.unmodifiableMap(downloads);
    }

    @Override
    public @NotNull Map<String, RemoteArtifact> getDownloads() {
        return downloads;
    }

    @Override
    public @NotNull String getName() {
        return mavenCoords;
    }
}
