package eu.software4you.ulib.spigot.util;

import eu.software4you.ulib.core.io.IOUtil;
import org.jetbrains.annotations.NotNull;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

/**
 * @deprecated this class is poorly designed
 */
// TODO: re-do this class -> maybe something like "MojangUtil" or "MojangRestAPI"?
@Deprecated(since = "3.0")
public class UUIDFetcher {

    @NotNull
    public static Optional<UUID> getUUID(@NotNull final String playername) {
        try (var in = URI.create("https://api.mojang.com/users/profiles/minecraft/" + playername).toURL().openStream()) {
            final String output = new String(IOUtil.read(new InputStreamReader(in)).orElseRethrow());
            final StringBuilder result = new StringBuilder();
            readData(output, result);
            final String u = result.toString();
            StringBuilder uuid = new StringBuilder();
            for (int i = 0; i <= 31; ++i) {
                uuid.append(u.charAt(i));
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    uuid.append("-");
                }
            }
            return Optional.of(UUID.fromString(uuid.toString()));
        } catch (Exception e) {
            // ignored
        }
        return Optional.empty();
    }

    private static void readData(final String toRead, final StringBuilder result) {
        for (int i = 7; i < 200 && !String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\""); ++i) {
            result.append(toRead.charAt(i));
        }
    }
}
