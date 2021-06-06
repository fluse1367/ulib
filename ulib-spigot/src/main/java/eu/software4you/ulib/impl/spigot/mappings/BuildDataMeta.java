package eu.software4you.ulib.impl.spigot.mappings;

import com.google.gson.*;
import com.google.gson.stream.JsonWriter;
import eu.software4you.http.CachedResource;
import eu.software4you.http.HttpUtil;
import eu.software4you.ulib.ULib;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Getter
final class BuildDataMeta {
    public static String SPIGOTMC_REST = "https://hub.spigotmc.org/stash/rest/api/1.0/projects/SPIGOT/repos/builddata";

    private final String mcVersion;
    private final String hash;
    private final long time;

    private final String cm;
    private final CachedResource classMappings;
    private final String mm;
    private final CachedResource memberMappings;

    private BuildDataMeta(String ver, String hash, long time, String cm, String mm) {
        this.mcVersion = ver;
        this.hash = hash;
        this.time = time;

        String mappingsUrl = SPIGOTMC_REST + "/raw/mappings/";

        this.classMappings = new CachedResource(mappingsUrl + (this.cm = cm) + "?at=" + hash, null, this::cachePath);
        this.memberMappings = new CachedResource(mappingsUrl + (this.mm = mm) + "?at=" + hash, null, this::cachePath);
    }

    private static BuildDataMeta fromJson(JsonObject json, String ver, String hash, long time) {
        String cm = json.get("classMappings").getAsString();
        String mm = json.get("memberMappings").getAsString();

        return new BuildDataMeta(ver, hash, time, cm, mm);
    }

    private static BuildDataMeta fromJson(JsonObject json, String ver) {
        String hash = json.get("hash").getAsString();
        long time = json.get("time").getAsLong();
        return fromJson(json, ver, hash, time);
    }

    @SneakyThrows
    private static BuildDataMeta fromCommit(String commitHash, long time) {
        String url = SPIGOTMC_REST + "/raw/info.json?at=" + commitHash;

        ULib.logger().fine(() -> "Requesting " + url);
        val in = new CachedResource(url, null, u ->
                String.format("bukkitbuilddata/%s/info.json", commitHash)).require();

        ULib.logger().finer(() -> "Loading JSON");
        JsonObject json;
        try (val reader = new InputStreamReader(in)) {
            json = JsonParser.parseReader(reader).getAsJsonObject();
        }

        // ensure json is valid
        if (!json.has("minecraftVersion"))
            return null;

        return fromJson(json, json.get("minecraftVersion").getAsString(), commitHash, time);
    }

    @SneakyThrows
    static Map<String, BuildDataMeta> loadBuildData() {
        val logger = ULib.logger();
        logger.fine(() -> "Loading bukkit build data...");

        // load cached meta
        JsonObject cachedMeta;
        File metaFile = new File(ULib.get().getCacheDir(), "bukkitbuilddata/meta.json");
        if (metaFile.exists()) {
            cachedMeta = JsonParser.parseReader(new FileReader(metaFile)).getAsJsonObject();
        } else {
            val dir = metaFile.getParentFile();
            if (!dir.exists()) {
                dir.mkdirs();
            }
            cachedMeta = new JsonObject();
            cachedMeta.add("versions", new JsonObject());
        }

        Map<String, BuildDataMeta> buildData = new HashMap<>();

        // load meta.json versions
        cachedMeta.get("versions").getAsJsonObject().entrySet().forEach(en -> {
            String ver = en.getKey();

            JsonObject json = en.getValue().getAsJsonObject();
            String hash = json.get("hash").getAsString();

            val meta = fromJson(json, ver);
            buildData.put(ver, meta);

            logger.fine(() -> String.format("meta.json: %s (%s) %s", ver, hash, meta));
        });

        String lastHash = cachedMeta.has("lastHash") ? cachedMeta.get("lastHash").getAsString() : null;
        String updatedLastHash = null;

        // download newer commits
        int start = 0;
        boolean hasNextPage;
        do {
            String url = String.format("%s/commits/?until=master&start=%d%s", SPIGOTMC_REST, start, lastHash != null ? "&since=" + lastHash : "");

            logger.fine(() -> "Commit chunk: " + url);

            val in = HttpUtil.getContent(url);
            JsonObject json;
            try (val reader = new InputStreamReader(in)) {
                json = JsonParser.parseReader(reader).getAsJsonObject();
            }

            hasNextPage = !json.get("isLastPage").getAsBoolean();
            val nps = json.get("nextPageStart");
            start = nps.isJsonNull() ? -1 : nps.getAsInt();

            JsonArray arr = json.getAsJsonArray("values");
            for (JsonElement e : arr) {
                JsonObject ee = e.getAsJsonObject();
                String hash = ee.get("id").getAsString();
                long time = ee.get("committerTimestamp").getAsLong();

                if (updatedLastHash == null)
                    updatedLastHash = hash;

                logger.fine(() -> "Loading " + hash);

                val meta = BuildDataMeta.fromCommit(hash, time);
                if (meta == null)
                    continue; // request failed

                logger.fine(() -> "Version " + meta.getMcVersion()
                        + " at time " + meta.getTime()
                        + ": " + meta);

                if (buildData.containsKey(meta.getMcVersion())) { // only overwrite handler meta if its older
                    val oldMeta = buildData.get(meta.getMcVersion());
                    logger.finer(() -> "Version already found");

                    if (!(meta.getTime() > oldMeta.getTime())) {
                        logger.finer(() -> "Skipping (not newer than already loaded one)");
                        continue;
                    }
                }

                buildData.put(meta.getMcVersion(), meta);

                // add entry to meta.json
                cachedMeta.get("versions").getAsJsonObject().add(meta.getMcVersion(), meta.toJson());
            }

            logger.fine(() -> "Chunk done");
        } while (hasNextPage);

        cachedMeta.addProperty("lastHash", updatedLastHash != null ? updatedLastHash : lastHash);

        // save meta.json

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        try (val writer = new JsonWriter(new FileWriter(metaFile, false))) {
            gson.toJson(cachedMeta, writer);
        }
        return buildData;
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("hash", hash);
        json.addProperty("time", time);
        json.addProperty("classMappings", cm);
        json.addProperty("memberMappings", mm);
        return json;
    }

    private String cachePath(URL url) {
        return String.format("bukkitbuilddata/%s/%s%s", mcVersion, url.getHost(), url.getPath());
    }

    @Override
    public String toString() {
        return "BuildDataMeta{" +
                "mcVersion='" + mcVersion + '\'' +
                ", hash='" + hash + '\'' +
                ", time=" + time +
                ", cm='" + cm + '\'' +
                ", mm='" + mm + '\'' +
                '}';
    }
}
