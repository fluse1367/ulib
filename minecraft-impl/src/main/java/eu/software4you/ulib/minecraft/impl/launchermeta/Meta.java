package eu.software4you.ulib.minecraft.impl.launchermeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import eu.software4you.ulib.core.api.http.CachedResource;
import eu.software4you.ulib.core.api.utils.LazyValue;
import eu.software4you.ulib.minecraft.api.launchermeta.VersionManifest;
import eu.software4you.ulib.minecraft.api.launchermeta.VersionsMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

final class Meta implements VersionsMeta {
    private final String release, snapshot;
    private final Map<String, LazyValue<Version>> versions;

    Meta(JsonObject json) {

        // versions loading
        Map<String, LazyValue<Version>> versions = new HashMap<>();
        json.getAsJsonArray("versions").forEach(e -> {
            var ver = e.getAsJsonObject();
            var id = ver.get("id").getAsString();
            var url = ver.get("url").getAsString();

            versions.put(id, new LazyValue<>(() -> new Version(url, JsonParser.parseReader(
                    new InputStreamReader(new CachedResource(url, null).require())).getAsJsonObject())));
        });
        this.versions = Collections.unmodifiableMap(versions);

        var latest = json.getAsJsonObject("latest");
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
                .map(LazyValue::getIfDone)
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
}
