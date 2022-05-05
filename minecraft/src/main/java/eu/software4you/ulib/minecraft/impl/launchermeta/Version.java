package eu.software4you.ulib.minecraft.impl.launchermeta;

import eu.software4you.ulib.core.common.Keyable;
import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.util.LazyValue;
import eu.software4you.ulib.minecraft.launchermeta.*;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

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
    private final LazyValue<Collection<RemoteLibrary>> libraries;

    @SneakyThrows
    Version(String url, JsonConfiguration json) {
        this.id = json.string("id").orElseThrow();

        var ai = json.getSub("assetIndex").orElseThrow();
        this.assetIndex = new Resource(ai.string("id").orElseThrow(), ai);

        this.type = Type.valueOf(json.string("type").orElseThrow().toUpperCase());
        this.url = new URL(url);

        this.time = OffsetDateTime.parse(json.string("time").orElseThrow());
        this.releaseTime = OffsetDateTime.parse(json.string("releaseTime").orElseThrow());

        Map<String, Resource> downloads = new HashMap<>();
        json.getSub("downloads").orElseThrow()
                .getSubs(false)
                .forEach(sub -> {
                    @SuppressWarnings("unchecked")
                    var key = ((Keyable<String>) sub).getKey();
                    downloads.put(key, new Resource(key, sub));
                });
        this.downloads = Collections.unmodifiableMap(downloads);

        this.libraries = LazyValue.immutable(() -> {
            var libs = json.list(JsonConfiguration.class, "libraries")
                    .orElseThrow().stream().map(sub -> {
                        var mvnc = sub.string("name").orElseThrow();
                        var dwnlds = sub.getSub("downloads").orElseThrow();
                        return new Library(mvnc, dwnlds);
                    })
                    .toList();
            //noinspection RedundantUnmodifiable
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
    public @NotNull Optional<RemoteResource> getDownload(@NotNull String id) {
        return Optional.ofNullable(downloads.get(id));
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
