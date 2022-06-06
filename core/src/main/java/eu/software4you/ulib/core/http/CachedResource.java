package eu.software4you.ulib.core.http;

import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.Expect;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.Optional;

/**
 * A http resource that may be cached.
 * <p>
 * Can perform validity checks based on sha1 sum.
 */
public class CachedResource extends ChecksumFile {
    @NotNull
    protected final URL url;

    /**
     * @param url    the url of the resource
     * @param sha1   the sha1 checksum of the resource, or {@code null}
     * @param prefix prefix of local cache location path
     */
    public CachedResource(@NotNull String url, @Nullable String sha1, @NotNull String prefix) {
        super("SHA-1", sha1, Internal.getCacheDir().toPath(), prefix, urlLoc(url));
        this.url = url(url);
    }

    /**
     * @param url  the url of the resource
     * @param sha1 the sha1 checksum of the resource, or {@code null}
     */
    public CachedResource(@NotNull String url, @Nullable String sha1) {
        this(url, sha1, "http");
    }

    @SneakyThrows
    private static URL url(String url) {
        return new URL(url);
    }

    private static Path urlLoc(String url) {
        var u = url(url);
        return Paths.get(u.getHost() + u.getPath());
    }

    @NotNull
    public URL getUrl() {
        return url;
    }

    @NotNull
    public Optional<String> getSha1() {
        return Optional.ofNullable(checksum);
    }

    @NotNull
    public Path getCacheLocation() {
        return fileLocation;
    }

    /**
     * Downloads the contents.
     *
     * @return the resource's contents
     */
    @NotNull
    public Expect<InputStream, IOException> request() {
        return Expect.compute(url::openStream);
    }

    @SneakyThrows
    @Override
    protected void generate() {
        // download to file
        Files.createDirectories(fileLocation.getParent());
        try (var in = request().orElseThrow();
             var out = Files.newOutputStream(fileLocation)) {
            IOUtil.write(in, out).rethrow();
        }
    }

}
