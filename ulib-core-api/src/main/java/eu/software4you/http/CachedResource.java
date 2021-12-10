package eu.software4you.ulib.core.api.http;

import eu.software4you.ulib.core.api.io.IOUtil;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static eu.software4you.ulib.ULib.logger;

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
        super("SHA-1", sha1, ULib.get().getCacheDir(), prefix, urlLoc(url));
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

    @Nullable
    public String getSha1() {
        return checksum;
    }

    @NotNull
    public File getCacheLocation() {
        return file;
    }

    /**
     * Downloads the contents.
     *
     * @return the resource's contents
     */
    @NotNull
    public InputStream request() {
        return HttpUtil.getContent(url.toString());
    }

    @SneakyThrows
    @Override
    protected void generate() {
        logger().finer(() -> String.format("Caching %s", file.getName()));

        // download to file
        mkdirsp(file);
        IOUtil.write(request(), new FileOutputStream(file));
    }

}
