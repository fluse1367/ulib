package eu.software4you.ulib.core.api.http;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.internal.Providers;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

/**
 * To use this class, you have to put the
 * <a href="https://github.com/apache/httpcomponents-client/">Apache HttpComponents HttpClient</a> into the classpath
 * (this is another "make the compiler happy" situation).
 */
public abstract class HttpUtil {

    private static HttpUtil getInstance() {
        return Providers.get(HttpUtil.class);
    }

    public static HttpClient buildBasicClient(String userAgent) {
        return getInstance().buildBasicClient0(userAgent);
    }

    @SafeVarargs
    public static String getContentAsString(String url, Pair<String, String>... headers) {
        return getInstance().getContentAsString0(url, headers);
    }

    @SafeVarargs
    public static InputStream getContent(String url, Pair<String, String>... headers) {
        return getInstance().getContent0(url, headers);
    }

    @SafeVarargs
    public static String getContentAsString(HttpClient client, String url, Pair<String, String>... headers) {
        return getInstance().getContentAsString0(client, url, headers);
    }

    @SafeVarargs
    public static InputStream getContent(HttpClient client, String url, Pair<String, String>... headers) {
        return getInstance().getContent0(client, url, headers);
    }

    @SafeVarargs
    public static HttpResponse get(String url, Pair<String, String>... headers) {
        return getInstance().get0(url, headers);
    }

    @SafeVarargs
    public static HttpResponse get(HttpClient client, String url, Pair<String, String>... headers) {
        return getInstance().get0(client, url, headers);
    }


    @SafeVarargs
    public static String postContentAsString(String url, Pair<String, String>... parameters) {
        return getInstance().postContentAsString0(url, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(String url, Pair<String, String>... parameters) {
        return getInstance().postContent0(url, parameters);
    }

    @SafeVarargs
    public static String postContentAsString(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return getInstance().postContentAsString0(url, headers, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return getInstance().postContent0(url, headers, parameters);
    }

    @SafeVarargs
    public static String postContentAsString(HttpClient client, String url, Pair<String, String>... parameters) {
        return getInstance().postContentAsString0(client, url, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(HttpClient client, String url, Pair<String, String>... parameters) {
        return getInstance().postContent0(client, url, parameters);
    }

    @SafeVarargs
    public static String postContentAsString(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return getInstance().postContentAsString0(client, url, headers, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return getInstance().postContent0(client, url, headers, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(String url, Pair<String, String>... parameters) {
        return getInstance().post0(url, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return getInstance().post0(url, headers, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(HttpClient client, String url, Pair<String, String>... parameters) {
        return post(client, url, Collections.emptyList(), parameters);
    }

    @SafeVarargs
    public static HttpResponse post(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return getInstance().post0(client, url, headers, parameters);
    }

    protected abstract HttpClient buildBasicClient0(String userAgent);

    protected abstract String getContentAsString0(String url, Pair<String, String>[] headers);

    protected abstract InputStream getContent0(String url, Pair<String, String>[] headers);

    protected abstract String getContentAsString0(HttpClient client, String url, Pair<String, String>[] headers);

    protected abstract InputStream getContent0(HttpClient client, String url, Pair<String, String>[] headers);

    protected abstract HttpResponse get0(String url, Pair<String, String>[] headers);

    protected abstract HttpResponse get0(HttpClient client, String url, Pair<String, String>[] headers);

    protected abstract HttpResponse get0(HttpClient client, String url, List<Pair<String, String>> headers);

    protected abstract String postContentAsString0(String url, Pair<String, String>[] parameters);

    protected abstract InputStream postContent0(String url, Pair<String, String>[] parameters);

    protected abstract String postContentAsString0(String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters);

    protected abstract InputStream postContent0(String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters);

    protected abstract String postContentAsString0(HttpClient client, String url, Pair<String, String>[] parameters);

    protected abstract InputStream postContent0(HttpClient client, String url, Pair<String, String>[] parameters);

    protected abstract String postContentAsString0(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters);

    protected abstract InputStream postContent0(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters);

    protected abstract HttpResponse post0(String url, Pair<String, String>[] parameters);

    protected abstract HttpResponse post0(String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters);

    protected abstract HttpResponse post0(HttpClient client, String url, Pair<String, String>[] parameters);

    protected abstract HttpResponse post0(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters);
}