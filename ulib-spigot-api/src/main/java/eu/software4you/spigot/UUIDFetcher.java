package eu.software4you.spigot;

import eu.software4you.http.HttpUtil;

import java.util.UUID;

public class UUIDFetcher {

    public static UUID getUUID(final String playername) {
        try {
            final String output = HttpUtil.getContentAsString("https://api.mojang.com/users/profiles/minecraft/" + playername);
            final StringBuilder result = new StringBuilder();
            readData(output, result);
            final String u = result.toString();
            String uuid = "";
            for (int i = 0; i <= 31; ++i) {
                uuid += u.charAt(i);
                if (i == 7 || i == 11 || i == 15 || i == 19) {
                    uuid += "-";
                }
            }
            return UUID.fromString(uuid);
        } catch (Exception e) {
        }
        return null;
    }

    private static void readData(final String toRead, final StringBuilder result) {
        for (int i = 7; i < 200 && !String.valueOf(toRead.charAt(i)).equalsIgnoreCase("\""); ++i) {
            result.append(toRead.charAt(i));
        }
    }
}
