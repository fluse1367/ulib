package eu.software4you.ulib.impl.minecraft.launchermeta;

import com.google.gson.JsonObject;
import eu.software4you.http.HttpUtil;
import eu.software4you.ulib.minecraft.launchermeta.RemoteResource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.net.URL;

@Getter
class Resource implements RemoteResource {
    private final String id;
    private final String sha1;
    private final long size;
    @Getter(AccessLevel.NONE)
    private final String urlStr;
    private final URL url;

    Resource(String id, JsonObject json) {
        this(
                id,
                json.get("sha1").getAsString(),
                json.get("size").getAsLong(),
                json.get("url").getAsString()
        );
    }

    @SneakyThrows
    Resource(String id, String sha1, long size, String url) {
        this.id = id;
        this.sha1 = sha1;
        this.size = size;
        this.url = new URL(urlStr = url);
    }

    @Override
    public InputStream download() {
        return HttpUtil.getContent(urlStr);
    }
}
