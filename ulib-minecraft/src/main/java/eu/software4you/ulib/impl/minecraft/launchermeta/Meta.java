package eu.software4you.ulib.impl.minecraft.launchermeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.software4you.http.CachedResource;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.minecraft.launchermeta.VersionManifest;
import eu.software4you.ulib.minecraft.launchermeta.VersionsMeta;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

final class Meta implements VersionsMeta {
    private final String release, snapshot;
    private final Map<String, Loader<Version>> versions;

    Meta(JsonObject json) {

        // versions loading
        Map<String, Loader<Version>> versions = new HashMap<>();
        json.getAsJsonArray("versions").forEach(e -> {
            val ver = e.getAsJsonObject();
            val id = ver.get("id").getAsString();
            val url = ver.get("url").getAsString();

            versions.put(id, new Loader<>(() -> new Version(url, JsonParser.parseReader(
                    new InputStreamReader(new CachedResource(url, null).require())).getAsJsonObject())));
        });
        this.versions = Collections.unmodifiableMap(versions);

        val latest = json.getAsJsonObject("latest");
        this.release = latest.get("release").getAsString();
        this.snapshot = latest.get("snapshot").getAsString();
    }

    @Override
    public @NotNull VersionManifest getRelease() {
        //noinspection ConstantConditions
        return get(release);
    }

    @Override
    public @NotNull VersionManifest getSnapshot() {
        //noinspection ConstantConditions
        return get(snapshot);
    }

    @Override
    public @Nullable VersionManifest get(@NotNull String id) {
        if (versions.containsKey(id))
            return versions.get(id).get();
        return null;
    }

    @Override
    public @NotNull Collection<VersionManifest> getVersions() {
        return Collections.unmodifiableCollection(versions.values().stream()
                .map(Loader::getIfDone)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
}
