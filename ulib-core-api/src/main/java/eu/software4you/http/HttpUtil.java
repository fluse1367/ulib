package eu.software4you.http;

import eu.software4you.common.collection.Pair;
import eu.software4you.ulib.Await;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public abstract class HttpUtil {
    @Await
    private static HttpUtil impl;

    public static HttpClient buildBasicClient(String userAgent) {
        return impl.buildBasicClient0(userAgent);
    }

    @SafeVarargs
    public static String getContentAsString(String url, Pair<String, String>... headers) {
        return impl.getContentAsString0(url, headers);
    }

    @SafeVarargs
    public static InputStream getContent(String url, Pair<String, String>... headers) {
        return impl.getContent0(url, headers);
    }

    @SafeVarargs
    public static String getContentAsString(HttpClient client, String url, Pair<String, String>... headers) {
        return impl.getContentAsString0(client, url, headers);
    }

    @SafeVarargs
    public static InputStream getContent(HttpClient client, String url, Pair<String, String>... headers) {
        return impl.getContent0(client, url, headers);
    }

    @SafeVarargs
    public static HttpResponse get(String url, Pair<String, String>... headers) {
        return impl.get0(url, headers);
    }

    @SafeVarargs
    public static HttpResponse get(HttpClient client, String url, Pair<String, String>... headers) {
        return impl.get0(client, url, headers);
    }


    @SafeVarargs
    public static String postContentAsString(String url, Pair<String, String>... parameters) {
        return impl.postContentAsString0(url, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(String url, Pair<String, String>... parameters) {
        return impl.postContent0(url, parameters);
    }

    @SafeVarargs
    public static String postContentAsString(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return impl.postContentAsString0(url, headers, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return impl.postContent0(url, headers, parameters);
    }

    @SafeVarargs
    public static String postContentAsString(HttpClient client, String url, Pair<String, String>... parameters) {
        return impl.postContentAsString0(client, url, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(HttpClient client, String url, Pair<String, String>... parameters) {
        return impl.postContent0(client, url, parameters);
    }

    @SafeVarargs
    public static String postContentAsString(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return impl.postContentAsString0(client, url, headers, parameters);
    }

    @SafeVarargs
    public static InputStream postContent(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return impl.postContent0(client, url, headers, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(String url, Pair<String, String>... parameters) {
        return impl.post0(url, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return impl.post0(url, headers, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(HttpClient client, String url, Pair<String, String>... parameters) {
        return post(client, url, Collections.emptyList(), parameters);
    }

    @SafeVarargs
    public static HttpResponse post(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return impl.post0(client, url, headers, parameters);
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
