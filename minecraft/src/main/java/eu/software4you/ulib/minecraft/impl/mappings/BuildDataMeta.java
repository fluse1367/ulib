package eu.software4you.ulib.minecraft.impl.mappings;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.http.CachedResource;
import eu.software4you.ulib.core.impl.Internal;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
final class BuildDataMeta {
    public static String SPIGOTMC_REST = "https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/builddata";

    private final String mcVersion;
    private final String hash;

    private final String cm;
    private final CachedResource classMappings;
    private final String mm;
    private final CachedResource memberMappings;

    private BuildDataMeta(String ver, String hash, String cm, String mm) {
        this.mcVersion = ver;
        this.hash = hash;

        String mappingsUrl = SPIGOTMC_REST + "/raw/mappings/";

        this.classMappings = new CachedResource(mappingsUrl + (this.cm = cm) + "?at=" + hash, null);
        this.memberMappings = (this.mm = mm) == null ? null : new CachedResource(mappingsUrl + this.mm + "?at=" + hash, null);
    }

    private static BuildDataMeta fromJson(JsonConfiguration json, String ver, String hash) {
        String cm = json.string("classMappings").orElseThrow();
        String mm = json.string("memberMappings").orElse(null);

        return new BuildDataMeta(ver, hash, cm, mm);
    }

    private static BuildDataMeta fromJson(JsonConfiguration json, String ver) {
        return fromJson(json, ver, json.string("hash").orElseThrow());
    }

    @SneakyThrows
    @Nullable
    private static BuildDataMeta fromCommit(String commitHash) {
        String url = SPIGOTMC_REST + "/raw/info.json?at=" + commitHash;

        var in = URI.create(url).toURL().openStream();

        JsonConfiguration json;
        try (var reader = new InputStreamReader(in)) {
            json = JsonConfiguration.loadJson(in).orElseRethrow();
        }

        // ensure json is valid
        if (!json.isSet("minecraftVersion"))
            return null;

        return fromJson(json, json.string("minecraftVersion").orElseThrow(), commitHash);
    }

    @SneakyThrows
    static BuildDataMeta loadBuildData(String ver) {

        String verKey = ver.replace(".", "_");

        // load cached meta
        JsonConfiguration cachedMeta;
        Path metaFile = Internal.getCacheDir().resolve("bukkitbuilddata/versions.json");
        if (Files.exists(metaFile)) {
            cachedMeta = JsonConfiguration.loadJson(metaFile).orElseThrow();
        } else {
            cachedMeta = JsonConfiguration.newJson();
        }

        // load from versions.json
        if (cachedMeta.isSub(verKey)) {
            return fromJson(cachedMeta.getSub(verKey).orElseThrow(), ver);
        }

        // download json data
        BuildDataMeta buildData;

        String url = String.format("https://hub.spigotmc.org/versions/%s.json", ver);
        try (var reader = new InputStreamReader(new CachedResource(url, null).require().orElseThrow())) {
            var json = JsonConfiguration.loadJson(reader).orElseThrow();
            String commit = json.string("refs.BuildData").orElseThrow();

            buildData = fromCommit(commit);
            if (buildData == null) {
                return null;
            }

            cachedMeta.set(verKey, buildData.toJson());
        }

        // save meta.json
        Files.createDirectories(metaFile.getParent());
        cachedMeta.dumpTo(metaFile).rethrow();
        return buildData;
    }

    public JsonConfiguration toJson() {
        var json = JsonConfiguration.newJson();
        json.set("hash", hash);
        json.set("classMappings", cm);
        json.set("memberMappings", mm);
        return json;
    }

    @Override
    public String toString() {
        return "BuildDataMeta{" +
               "mcVersion='" + mcVersion + '\'' +
               ", hash='" + hash + '\'' +
               ", cm='" + cm + '\'' +
               ", mm='" + mm + '\'' +
               '}';
    }
}
