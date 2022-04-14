package eu.software4you.ulib.spigot.impl.mappings;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import eu.software4you.ulib.core.http.CachedResource;
import eu.software4you.ulib.core.impl.Internal;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.net.URI;
import java.util.Optional;

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

    private static BuildDataMeta fromJson(JsonObject json, String ver, String hash) {
        String cm = json.get("classMappings").getAsString();
        String mm = Optional.ofNullable(json.get("memberMappings"))
                .map(JsonElement::getAsString)
                .orElse(null);

        return new BuildDataMeta(ver, hash, cm, mm);
    }

    private static BuildDataMeta fromJson(JsonObject json, String ver) {
        return fromJson(json, ver, json.get("hash").getAsString());
    }

    @SneakyThrows
    private static BuildDataMeta fromCommit(String commitHash) {
        String url = SPIGOTMC_REST + "/raw/info.json?at=" + commitHash;

        var in = URI.create(url).toURL().openStream();

        JsonObject json;
        try (var reader = new InputStreamReader(in)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        }

        // ensure json is valid
        if (!json.has("minecraftVersion"))
            return null;

        return fromJson(json, json.get("minecraftVersion").getAsString(), commitHash);
    }

    @SneakyThrows
    static BuildDataMeta loadBuildData(String ver) {

        // load cached meta
        JsonObject cachedMeta;
        File metaFile = new File(Internal.getCacheDir(), "bukkitbuilddata/versions.json");
        if (metaFile.exists()) {
            cachedMeta = JsonParser.parseReader(new FileReader(metaFile)).getAsJsonObject();
        } else {
            cachedMeta = new JsonObject();
        }

        // load from versions.json
        if (cachedMeta.has(ver)) {
            var json = cachedMeta.getAsJsonObject(ver);
            return fromJson(json, ver);
        }

        // download json data
        BuildDataMeta buildData;

        String url = String.format("https://hub.spigotmc.org/versions/%s.json", ver);
        try (var reader = new InputStreamReader(new CachedResource(url, null).require().orElseThrow())) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            String commit = json.getAsJsonObject("refs").get("BuildData").getAsString();

            buildData = fromCommit(commit);
            if (buildData == null) {
                return null;
            }

            cachedMeta.add(ver, buildData.toJson());
        }

        // save meta.json
        var dir = metaFile.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try (var writer = new JsonWriter(new FileWriter(metaFile, false))) {
            gson.toJson(cachedMeta, writer);
        }
        return buildData;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("hash", hash);
        json.addProperty("classMappings", cm);
        json.addProperty("memberMappings", mm);
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
