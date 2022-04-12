package eu.software4you.ulib.minecraft.impl.launchermeta;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.http.CachedResource;
import eu.software4you.ulib.core.util.Expect;
import eu.software4you.ulib.minecraft.launchermeta.RemoteResource;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

@Getter
class Resource extends CachedResource implements RemoteResource {
    @NotNull
    private final String id;
    private final long size;
    @Getter(AccessLevel.NONE)
    private final String urlStr;

    Resource(String id, JsonConfiguration json) {
        this(
                id,
                json.string("sha1").orElseThrow(),
                json.int64("size").orElseThrow(),
                json.string("url").orElseThrow()
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
    public Expect<InputStream, IOException> download() {
        // raw type for compat
        //noinspection unchecked,rawtypes
        return ((Expect) super.require());
    }
}
