package eu.software4you.ulib.impl.minecraft.launchermeta;

import com.google.gson.JsonObject;
import eu.software4you.ulib.minecraft.launchermeta.RemoteArtifact;
import eu.software4you.ulib.minecraft.launchermeta.RemoteLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class Library implements RemoteLibrary {
    private final String mavenCoords;
    private final Map<String, RemoteArtifact> downloads;

    Library(String mavenCoords, JsonObject json /*downloads*/) {
        this.mavenCoords = mavenCoords;

        Map<String, Artifact> downloads = new HashMap<>();
        var arti = json.get("artifact").getAsJsonObject();
        downloads.put("artifact", new Artifact("artifact", arti.get("path").getAsString(), arti));

        if (json.has("classifiers")) {
            json.getAsJsonObject("classifiers").entrySet().forEach(en -> {
                var sub = en.getValue().getAsJsonObject();
                String id = en.getKey();
                downloads.put(id, new Artifact(id, sub.get("path").getAsString(), sub));
            });
        }

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
