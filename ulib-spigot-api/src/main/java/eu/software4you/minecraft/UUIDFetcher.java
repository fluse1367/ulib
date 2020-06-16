package eu.software4you.minecraft;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.UUID;

public class UUIDFetcher {

    public static UUID getUUID(final String playername) {
        try {
            final String output = callURL("https://api.mojang.com/users/profiles/minecraft/" + playername);
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

    private static String callURL(final String URL) {
        final StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            final URL url = new URL(URL);
            urlConn = url.openConnection();
            if (urlConn != null) {
                urlConn.setReadTimeout(60000);
            }
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(), Charset.defaultCharset());
                final BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            return null;
        }
        return sb.toString();
    }
}
