package eu.software4you.ulib.impl.spigot.mappings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import eu.software4you.ulib.core.api.http.CachedResource;
import eu.software4you.ulib.core.api.http.HttpUtil;
import eu.software4you.ulib.ULib;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;

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
        this.memberMappings = new CachedResource(mappingsUrl + (this.mm = mm) + "?at=" + hash, null);
    }

    private static BuildDataMeta fromJson(JsonObject json, String ver, String hash) {
        String cm = json.get("classMappings").getAsString();
        String mm = json.get("memberMappings").getAsString();

        return new BuildDataMeta(ver, hash, cm, mm);
    }

    private static BuildDataMeta fromJson(JsonObject json, String ver) {
        return fromJson(json, ver, json.get("hash").getAsString());
    }

    @SneakyThrows
    private static BuildDataMeta fromCommit(String commitHash) {
        String url = SPIGOTMC_REST + "/raw/info.json?at=" + commitHash;

        ULib.logger().fine(() -> "Requesting " + url);
        var in = HttpUtil.getContent(url);

        ULib.logger().finer(() -> "Loading JSON");
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
        var logger = ULib.logger();
        logger.fine(() -> "Loading bukkit build data for " + ver);

        // load cached meta
        JsonObject cachedMeta;
        File metaFile = new File(ULib.get().getCacheDir(), "bukkitbuilddata/versions.json");
        if (metaFile.exists()) {
            cachedMeta = JsonParser.parseReader(new FileReader(metaFile)).getAsJsonObject();
        } else {
            cachedMeta = new JsonObject();
        }

        // load from versions.json
        if (cachedMeta.has(ver)) {
            var json = cachedMeta.getAsJsonObject(ver);
            var data = fromJson(json, ver);
            logger.finer(() -> String.format("From versions.json: %s", data));
            return data;
        }

        // download json data
        BuildDataMeta buildData;

        String url = String.format("https://hub.spigotmc.org/versions/%s.json", ver);
        logger.fine(() -> String.format("Loading %s from %s", ver, url));
        try (var reader = new InputStreamReader(new CachedResource(url, null).require())) {
            var json = JsonParser.parseReader(reader).getAsJsonObject();
            String commit = json.getAsJsonObject("refs").get("BuildData").getAsString();

            buildData = fromCommit(commit);
            if (buildData == null) {
                logger.fine(() -> "Request failed");
                return null;
            }

            logger.finer(buildData::toString);

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
