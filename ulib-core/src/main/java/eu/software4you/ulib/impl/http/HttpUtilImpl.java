package eu.software4you.ulib.impl.http;

import eu.software4you.common.collection.Pair;
import eu.software4you.http.HttpUtil;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Impl(HttpUtil.class)
final class HttpUtilImpl extends HttpUtil {
    private final HttpClient utilsHttpClient = buildBasicClient0(String.format("uLib/%s", ULib.get().getVersion()));

    protected HttpClient buildBasicClient0(String userAgent) {
        return HttpClientBuilder.create().setUserAgent(userAgent).build();
    }

    protected String getContentAsString0(String url, Pair<String, String>[] headers) {
        return toString(getContent(url, headers));
    }

    protected InputStream getContent0(String url, Pair<String, String>[] headers) {
        return content(get0(url, headers));
    }


    protected String getContentAsString0(HttpClient client, String url, Pair<String, String>[] headers) {
        return toString(getContent(client, url, headers));
    }

    protected InputStream getContent0(HttpClient client, String url, Pair<String, String>[] headers) {
        return content(get0(client, url, headers));
    }

    protected HttpResponse get0(String url, Pair<String, String>[] headers) {
        return get0(utilsHttpClient, url, Arrays.asList(headers));
    }

    protected HttpResponse get0(HttpClient client, String url, Pair<String, String>[] headers) {
        return get0(client, url, Arrays.asList(headers));
    }

    protected HttpResponse get0(HttpClient client, String url, List<Pair<String, String>> headers) {
        HttpGet request = new HttpGet(url);
        headers.forEach(h -> request.addHeader(h.getFirst(), h.getSecond()));
        return execute(client, request);
    }


    protected String postContentAsString0(String url, Pair<String, String>[] parameters) {
        return toString(postContent(url, parameters));
    }

    protected InputStream postContent0(String url, Pair<String, String>[] parameters) {
        return content(post0(url, parameters));
    }

    protected String postContentAsString0(String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters) {
        return toString(postContent(url, headers, parameters));
    }

    protected InputStream postContent0(String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters) {
        return content(post0(url, headers, parameters));
    }

    protected String postContentAsString0(HttpClient client, String url, Pair<String, String>[] parameters) {
        return toString(postContent(client, url, parameters));
    }

    protected InputStream postContent0(HttpClient client, String url, Pair<String, String>[] parameters) {
        return content(post0(client, url, parameters));
    }

    protected String postContentAsString0(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters) {
        return toString(postContent(client, url, headers, parameters));
    }

    protected InputStream postContent0(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters) {
        return content(post0(client, url, headers, parameters));
    }

    protected HttpResponse post0(String url, Pair<String, String>[] parameters) {
        return post0(utilsHttpClient, url, Collections.emptyList(), parameters);
    }

    protected HttpResponse post0(String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters) {
        return post0(utilsHttpClient, url, headers, parameters);
    }

    protected HttpResponse post0(HttpClient client, String url, Pair<String, String>[] parameters) {
        return post0(client, url, Collections.emptyList(), parameters);
    }

    protected HttpResponse post0(HttpClient client, String url, List<Pair<String, String>> headers, Pair<String, String>[] parameters) {
        List<NameValuePair> params = new ArrayList<>();
        for (Pair<String, String> param : parameters) {
            params.add(new BasicNameValuePair(param.getFirst(), param.getSecond()));
        }
        return post(client, url, headers, params);
    }

    private HttpResponse post(HttpClient client, String url, List<Pair<String, String>> headers, List<NameValuePair> parameters) {
        HttpPost request = new HttpPost(url);
        headers.forEach(h -> request.addHeader(h.getFirst(), h.getSecond()));
        request.setEntity(new UrlEncodedFormEntity(parameters, Consts.UTF_8));
        return execute(client, request);
    }

    private HttpResponse execute(HttpClient client, HttpEntityEnclosingRequestBase request) {
        HttpEntity ent = request.getEntity();
        if (ent != null) {
            request.setHeader(ent.getContentType());
            request.setHeader(ent.getContentEncoding());
        }
        return execute(client, (HttpRequestBase) request);
    }

    @SneakyThrows
    private HttpResponse execute(HttpClient client, HttpRequestBase request) {
        request.addHeader("Host", request.getURI().getHost());
        request.setConfig(RequestConfig.custom()
                .setSocketTimeout((int) TimeUnit.SECONDS.toMillis(10))
                .setConnectTimeout((int) TimeUnit.SECONDS.toMillis(10))
                .setConnectionRequestTimeout((int) TimeUnit.SECONDS.toMillis(10))
                .build());
        return client.execute(request);
    }

    @SneakyThrows
    private InputStream content(HttpResponse res) {
        val status = res.getStatusLine();
        val code = status.getStatusCode();
        if (code < 200 || code > 299)
            throw new HttpResponseException(code, status.getReasonPhrase());
        val entity = res.getEntity();
        Objects.requireNonNull(entity, "No content");
        return entity.getContent();
    }

    @SneakyThrows
    private String toString(InputStream stream) {
        String str = IOUtils.toString(stream, Charset.defaultCharset());
        stream.close();
        return str;
    }
}
