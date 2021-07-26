package eu.software4you.ulib.impl.minecraft.launchermeta;

import com.google.gson.JsonObject;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.minecraft.launchermeta.RemoteLibrary;
import eu.software4you.ulib.minecraft.launchermeta.RemoteResource;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.*;

final class Version implements VersionManifest {
    private final String id;
    private final RemoteResource assetIndex;
    private final Type type;
    private final URL url;
    private final OffsetDateTime time;
    private final OffsetDateTime releaseTime;
    private final Map<String, RemoteResource> downloads;
    private final Loader<Collection<RemoteLibrary>> libraries;

    @SneakyThrows
    Version(String url, JsonObject json) {
        this.id = json.get("id").getAsString();

        var ai = json.get("assetIndex").getAsJsonObject();
        this.assetIndex = new Resource(ai.get("id").getAsString(), ai);

        this.type = Type.valueOf(json.get("type").getAsString().toUpperCase());
        this.url = new URL(url);

        this.time = OffsetDateTime.parse(json.get("time").getAsString());
        this.releaseTime = OffsetDateTime.parse(json.get("releaseTime").getAsString());

        Map<String, Resource> downloads = new HashMap<>();
        json.getAsJsonObject("downloads").entrySet().forEach(en -> {
            downloads.put(en.getKey(), new Resource(en.getKey(), en.getValue().getAsJsonObject()));
        });
        this.downloads = Collections.unmodifiableMap(downloads);

        this.libraries = new Loader<>(() -> {
            List<Library> libs = new ArrayList<>();

            json.getAsJsonArray("libraries").forEach(e -> {
                var lib = e.getAsJsonObject();
                var mvnc = lib.get("name").getAsString();
                var dwnlds = lib.get("downloads").getAsJsonObject();
                libs.add(new Library(mvnc, dwnlds));
            });

            return Collections.unmodifiableCollection(libs);
        });
    }

    @Override
    public @NotNull String getId() {
        return id;
    }

    @Override
    public @NotNull RemoteResource getAssetIndex() {
        return assetIndex;
    }

    @Override
    public @NotNull Type getType() {
        return type;
    }

    @Override
    public @NotNull URL getUrl() {
        return url;
    }

    @Override
    public @NotNull OffsetDateTime getTime() {
        return time;
    }

    @Override
    public @NotNull OffsetDateTime getReleaseTime() {
        return releaseTime;
    }

    @Override
    public @Nullable RemoteResource getDownload(@NotNull String id) {
        return downloads.get(id);
    }

    @Override
    public @NotNull Map<String, RemoteResource> getDownloads() {
        return downloads;
    }

    @Override
    public @NotNull Collection<RemoteLibrary> getLibraries() {
        return libraries.get();
    }
}
