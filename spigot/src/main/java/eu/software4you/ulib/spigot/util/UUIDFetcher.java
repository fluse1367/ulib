package eu.software4you.ulib.spigot.util;

import eu.software4you.ulib.core.io.IOUtil;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.UUID;

public class UUIDFetcher {

    public static UUID getUUID(final String playername) {
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
            return UUID.fromString(uuid.toString());
        } catch (Exception e) {
            // ignored
        }
        return null;
    }

    private static void readData(final String toRead, final StringBuilder result) {
        for (int i = 7; i < 200 && !String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\""); ++i) {
            result.append(toRead.charAt(i));
        }
    }
}
