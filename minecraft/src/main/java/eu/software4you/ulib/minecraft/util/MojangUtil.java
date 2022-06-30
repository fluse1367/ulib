package eu.software4you.ulib.minecraft.util;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.http.HttpUtil;
import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.Conversions;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.*;

/**
 * Access to some portion mojang's rest api.
 */
public final class MojangUtil {

    private static final URI API = URI.create("https://api.mojang.com/");
    private static final URI SESSION = URI.create("https://sessionserver.mojang.com/");

    private static final URI PROFILE = API.resolve("users/profiles/minecraft/");
    private static final URI PROFILES = API.resolve("user/profiles/");


    /**
     * Fetches the UUID corresponding to the username.
     *
     * @param username the username to fetch the uuid for
     * @return the uuid
     */
    @NotNull
    public static Expect<UUID, ?> fetchUUID(@NotNull String username) {
        return HttpUtil.GET(PROFILE.resolve(username))
                .map(HttpResponse::body)
                .map(IOUtil::toString)
                .map(Expect::orElseRethrow)
                .map(StringReader::new)
                .map(JsonConfiguration::loadJson)
                .map(Expect::orElseRethrow)
                .map(json -> json.string("id")
                        .map(Conversions::hexToUUID)
                        .orElse(null)
                );
    }

    /**
     * Fetches all usernames a player had in the past and the current username.
     *
     * @param uuid the player's uuid
     * @return a list containing pairs of (username, optional: instant)
     */
    @NotNull
    public static Expect<List<Pair<String, Optional<Instant>>>, ?> fetchNames(@NotNull UUID uuid) {

        // strip dashes
        String uuidStr = uuid.toString().replace("-", "");

        return HttpUtil.GET(PROFILES.resolve(uuidStr + "/names"))
                .map(HttpResponse::body)
                .map(JsonConfiguration::loadJson)
                .map(Expect::orElseRethrow)
                .map(json -> json.list(JsonConfiguration.class, "").orElse(null))
                .map(list -> list.stream()
                        .map(sub -> new Pair<>(
                                sub.string("name").orElseThrow(),
                                sub.int64("changedToAt").map(Instant::ofEpochMilli)))
                        .toList()
                );
    }


}
