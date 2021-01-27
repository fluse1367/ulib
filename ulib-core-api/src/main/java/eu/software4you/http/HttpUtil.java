package eu.software4you.http;

import eu.software4you.common.collection.Pair;
import eu.software4you.ulib.ULib;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HttpUtil {
    private static final HttpClient utilsHttpClient = buildBasicClient(String.format("uLib/%s", ULib.get().getVersion()));

    public static HttpClient buildBasicClient(String userAgent) {
        return HttpClientBuilder.create().setUserAgent(userAgent).build();
    }

    @SafeVarargs
    public static String getContentAsString(String url, Pair<String, String>... headers) {
        return toString(getContent(url, headers));
    }

    @SafeVarargs
    public static InputStream getContent(String url, Pair<String, String>... headers) {
        return content(get(url, headers).getEntity());
    }

    @SafeVarargs
    public static String getContentAsString(HttpClient client, String url, Pair<String, String>... headers) {
        return toString(getContent(client, url, headers));
    }

    @SafeVarargs
    public static InputStream getContent(HttpClient client, String url, Pair<String, String>... headers) {
        return content(get(client, url, headers).getEntity());
    }

    @SafeVarargs
    public static HttpResponse get(String url, Pair<String, String>... headers) {
        return get(utilsHttpClient, url, Arrays.asList(headers));
    }

    @SafeVarargs
    public static HttpResponse get(HttpClient client, String url, Pair<String, String>... headers) {
        return get(client, url, Arrays.asList(headers));
    }

    private static HttpResponse get(HttpClient client, String url, List<Pair<String, String>> headers) {
        HttpGet request = new HttpGet(url);
        headers.forEach(h -> request.addHeader(h.getFirst(), h.getSecond()));
        return execute(client, request);
    }


    @SafeVarargs
    public static String postContentAsString(String url, Pair<String, String>... parameters) {
        return toString(postContent(url, parameters));
    }

    @SafeVarargs
    public static InputStream postContent(String url, Pair<String, String>... parameters) {
        return content(post(url, parameters).getEntity());
    }

    @SafeVarargs
    public static String postContentAsString(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return toString(postContent(url, headers, parameters));
    }

    @SafeVarargs
    public static InputStream postContent(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return content(post(url, headers, parameters).getEntity());
    }

    @SafeVarargs
    public static String postContentAsString(HttpClient client, String url, Pair<String, String>... parameters) {
        return toString(postContent(client, url, parameters));
    }

    @SafeVarargs
    public static InputStream postContent(HttpClient client, String url, Pair<String, String>... parameters) {
        return content(post(client, url, parameters).getEntity());
    }

    @SafeVarargs
    public static String postContentAsString(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return toString(postContent(client, url, headers, parameters));
    }

    @SafeVarargs
    public static InputStream postContent(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return content(post(client, url, headers, parameters).getEntity());
    }

    @SafeVarargs
    public static HttpResponse post(String url, Pair<String, String>... parameters) {
        return post(utilsHttpClient, url, Collections.emptyList(), parameters);
    }

    @SafeVarargs
    public static HttpResponse post(String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        return post(utilsHttpClient, url, headers, parameters);
    }

    @SafeVarargs
    public static HttpResponse post(HttpClient client, String url, Pair<String, String>... parameters) {
        return post(client, url, Collections.emptyList(), parameters);
    }

    @SafeVarargs
    public static HttpResponse post(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>... parameters) {
        List<NameValuePair> params = new ArrayList<>();
        for (Pair<String, String> param : parameters) {
            params.add(new BasicNameValuePair(param.getFirst(), param.getSecond()));
        }
        return post(client, url, headers, params);
    }

    private static HttpResponse post(HttpClient client, String url, List<Pair<String, String>> headers, List<NameValuePair> parameters) {
        HttpPost request = new HttpPost(url);
        headers.forEach(h -> request.addHeader(h.getFirst(), h.getSecond()));
        request.setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
        return execute(client, request);
    }

    private static HttpResponse execute(HttpClient client, HttpEntityEnclosingRequestBase request) {
        HttpEntity ent = request.getEntity();
        if (ent != null) {
            request.setHeader(ent.getContentType());
            request.setHeader(ent.getContentEncoding());
        }
        return execute(client, (HttpUriRequest) request);
    }

    private static HttpResponse execute(HttpClient client, HttpUriRequest request) {
        try {
            request.addHeader("Host", request.getURI().getHost());
            return client.execute(request);
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private static InputStream content(HttpEntity entity) {
        if (entity == null)
            throw new Error("No content");
        try {
            return entity.getContent();
        } catch (IOException e) {
            throw new Error(e);
        }
    }

    private static String toString(InputStream stream) {
        try {
            String str = IOUtils.toString(stream);
            stream.close();
            return str;
        } catch (IOException e) {
            throw new Error(e);
        }
    }

}
