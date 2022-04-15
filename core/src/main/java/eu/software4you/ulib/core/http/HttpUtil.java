package eu.software4you.ulib.core.http;

import eu.software4you.ulib.core.util.Expect;

import java.io.InputStream;
import java.net.URI;
import java.net.http.*;
import java.util.Map;
import java.util.StringJoiner;

import static java.net.URLEncoder.encode;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * A class to quickly send simple POST and GET requests.
 */
public class HttpUtil {
    private static final HttpClient DEFAULT_CLIENT = HttpClient.newHttpClient();

    /**
     * Sends a {@code application/x-www-form-urlencoded} POST request.
     * This method will also put the {@code Content-Length} header.
     * The key-value fields will be appended to in an url encoded format.
     *
     * @param uri    the uri
     * @param fields the key-value fields to send
     * @return the response
     * @see Map#of(Object, Object)
     */
    public static Expect<HttpResponse<InputStream>, Exception> POST(URI uri, Map<String, String> fields) {
        return Expect.compute(() -> {
            var body = x_www_form_urlencoded(fields).getBytes(UTF_8);

            var request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(body))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Content-Length", String.valueOf(body.length))
                    .build();

            return DEFAULT_CLIENT.send(request, ofInputStream());
        });
    }

    /**
     * Sends a GET request. The key-value fields will be appended to the base uri in an url encoded format.
     *
     * @param baseUri the base uri
     * @param fields  the key-value fields to send.
     * @return the response
     * @see Map#of(Object, Object)
     */
    public static Expect<HttpResponse<InputStream>, Exception> GET(URI baseUri, Map<String, String> fields) {
        return Expect.compute(() -> {
            var connect = baseUri.toString().endsWith("/") ? "?" : "/?";
            var uri = baseUri.resolve(connect.concat(x_www_form_urlencoded(fields)));
            return GET(uri).orElseRethrow();
        });
    }

    /**
     * Sends a GET request.
     *
     * @param uri the uri
     * @return the response
     */
    public static Expect<HttpResponse<InputStream>, Exception> GET(URI uri) {
        return Expect.compute(() -> {
            var request = HttpRequest.newBuilder(uri).GET().build();
            return DEFAULT_CLIENT.send(request, ofInputStream());
        });
    }

    private static String x_www_form_urlencoded(Map<String, String> fields) {
        var sj = new StringJoiner("&");
        fields.forEach((k, v) -> sj.add("%s=%s".formatted(encode(k, UTF_8), encode(v, UTF_8))));
        return sj.toString();
    }
}
