package eu.software4you.ulib.core.http;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

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
     * Sends a {@code application/x-www-form-urlencoded} POST request using the default http client.
     * The key-value fields will be appended to in an url encoded format.
     *
     * @param uri    the uri
     * @param fields the key-value fields to send
     * @return the response
     * @see #POST(URI, Map, HttpClient)
     * @see Map#of(Object, Object)
     */
    @NotNull
    public static Expect<HttpResponse<InputStream>, Exception> POST(@NotNull URI uri, @NotNull Map<String, String> fields) {
        return POST(uri, fields, DEFAULT_CLIENT);
    }

    /**
     * Sends a {@code application/x-www-form-urlencoded} POST request using the given http client.
     * The key-value fields will be appended to in an url encoded format.
     *
     * @param uri    the uri
     * @param fields the key-value fields to send
     * @param client the client to send the request with
     * @return the response
     * @see Map#of(Object, Object)
     */
    @NotNull
    public static Expect<HttpResponse<InputStream>, Exception> POST(@NotNull URI uri, @NotNull Map<String, String> fields,
                                                                    @NotNull HttpClient client) {
        return Expect.compute(() -> {
            var request = HttpRequest.newBuilder(uri)
                    .POST(HttpRequest.BodyPublishers.ofByteArray(formUrlEncode(fields).getBytes(UTF_8)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            return client.send(request, ofInputStream());
        });
    }

    /**
     * Sends a GET request using the default http client.
     * The key-value fields will be appended to the base uri in an url encoded format.
     *
     * @param baseUri the base uri
     * @param fields  the key-value fields to send.
     * @return the response
     * @see Map#of(Object, Object)
     */
    @NotNull
    public static Expect<HttpResponse<InputStream>, Exception> GET(@NotNull URI baseUri, @NotNull Map<String, String> fields) {
        return GET(baseUri, fields, DEFAULT_CLIENT);
    }

    /**
     * Sends a GET request using the given http client.
     * The key-value fields will be appended to the base uri in an url encoded format.
     *
     * @param baseUri the base uri
     * @param fields  the key-value fields to send.
     * @param client  the client to send the request with
     * @return the response
     * @see Map#of(Object, Object)
     */
    @NotNull
    public static Expect<HttpResponse<InputStream>, Exception> GET(@NotNull URI baseUri, @NotNull Map<String, String> fields,
                                                                   @NotNull HttpClient client) {
        return Expect.compute(() -> {
            var connect = baseUri.toString().endsWith("/") ? "?" : "/?";
            var uri = baseUri.resolve(connect.concat(formUrlEncode(fields)));
            return GET(uri, client).orElseRethrow();
        });
    }

    /**
     * Sends a GET request using the default http client.
     *
     * @param uri the uri
     * @return the response
     * @see #GET(URI, HttpClient)
     */
    @NotNull
    public static Expect<HttpResponse<InputStream>, Exception> GET(@NotNull URI uri) {
        return GET(uri, DEFAULT_CLIENT);
    }

    /**
     * Sends a GET request using the given http client.
     *
     * @param uri    the uri
     * @param client the client to send the request with
     * @return the response
     */
    @NotNull
    public static Expect<HttpResponse<InputStream>, Exception> GET(@NotNull URI uri, @NotNull HttpClient client) {
        return Expect.compute(() -> {
            var request = HttpRequest.newBuilder(uri).GET().build();
            return client.send(request, ofInputStream());
        });
    }

    /**
     * Encodes a key-value map into the {@code x-www-form-urlencoded} standard.
     *
     * @param fields the key-value map
     * @return the url encoded string
     * @see <a href="https://www.w3.org/TR/html401/interact/forms.html#h-17.13.4.1">W3 reference</a>
     */
    @NotNull
    public static String formUrlEncode(@NotNull Map<String, String> fields) {
        var sj = new StringJoiner("&");
        fields.forEach((k, v) -> sj.add("%s=%s".formatted(encode(k, UTF_8), encode(v, UTF_8))));
        return sj.toString();
    }
}
