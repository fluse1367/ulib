package eu.software4you.ulib.minecraft.impl.launchermeta;

import com.google.gson.JsonObject;
import eu.software4you.ulib.core.api.http.CachedResource;
import eu.software4you.ulib.minecraft.api.launchermeta.RemoteResource;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

@Getter
class Resource extends CachedResource implements RemoteResource {
    @NotNull
    private final String id;
    private final long size;
    @Getter(AccessLevel.NONE)
    private final String urlStr;

    Resource(String id, JsonObject json) {
        this(
                id,
                json.get("sha1").getAsString(),
                json.get("size").getAsLong(),
                json.get("url").getAsString()
        );
    }

    @SneakyThrows
    Resource(@NotNull String id, String sha1, long size, String url) {
        super(url, sha1);
        this.id = id;
        this.size = size;
        this.urlStr = url;
    }

    @SneakyThrows
    @Override
    @NotNull
    public InputStream download() {
        return super.require();
    }
}
