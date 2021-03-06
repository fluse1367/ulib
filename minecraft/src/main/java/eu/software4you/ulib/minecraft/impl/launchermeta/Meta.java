package eu.software4you.ulib.minecraft.impl.launchermeta;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.http.CachedResource;
import eu.software4you.ulib.core.util.LazyValue;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.*;

public final class Meta implements VersionsMeta {

    public static LazyValue<Meta> INSTANCE = LazyValue.immutable(() -> new Meta(
            JsonConfiguration.loadJson(URI.create("https://launchermeta.mojang.com/mc/game/version_manifest.json").toURL().openStream())
                    .orElseThrow()
    ));


    private final String release, snapshot;
    private final Map<String, LazyValue<Version>> versions;

    private Meta(JsonConfiguration json) {

        // versions loading
        Map<String, LazyValue<Version>> versions = new HashMap<>();
        json.list(JsonConfiguration.class, "versions")
                .orElseThrow()
                .forEach(sub -> {
                    var id = sub.string("id").orElseThrow();
                    var url = sub.string("url").orElseThrow();

                    versions.put(id, LazyValue.immutable(() -> new Version(url,
                            JsonConfiguration.loadJson(new CachedResource(url, null).require().orElseThrow())
                                    .orElseThrow()
                    )));
                });
        this.versions = Collections.unmodifiableMap(versions);

        var latest = json.getSub("latest").orElseThrow();
        this.release = latest.string("release").orElseThrow();
        this.snapshot = latest.string("snapshot").orElseThrow();
    }

    @Override
    public @NotNull VersionManifest getRelease() {
        return get(release).orElseThrow();
    }

    @Override
    public @NotNull VersionManifest getSnapshot() {
        return get(snapshot).orElseThrow();
    }

    @Override
    public @NotNull Optional<VersionManifest> get(@NotNull String id) {
        if (versions.containsKey(id))
            return Optional.ofNullable(versions.get(id).get());
        return null;
    }

    @SuppressWarnings("RedundantUnmodifiable")
    @Override
    public @NotNull Collection<VersionManifest> getVersions() {
        return Collections.unmodifiableCollection(versions.values().stream()
                .map(LazyValue::getIfAvailable)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    }
}
