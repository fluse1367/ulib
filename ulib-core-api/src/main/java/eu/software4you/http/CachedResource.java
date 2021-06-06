package eu.software4you.http;

import eu.software4you.ulib.ULib;
import eu.software4you.utils.ChecksumUtils;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.security.MessageDigest;

/**
 * A http resource that may be cached.
 * <p>
 * Can perform validity checks based on sha1 sum.
 */
public class CachedResource {
    @NotNull
    protected final URL url;
    @Nullable
    protected final String sha1;
    @NotNull
    protected final File file;

    /**
     * @param url  the url of the resource
     * @param sha1 the sha1 checksum of the resource, or {@code null}
     */
    public CachedResource(@NotNull URL url, @Nullable String sha1) {
        this.url = url;
        this.sha1 = sha1;
        this.file = new File(ULib.get().getCacheDir(), String.format("http/%s%s", url.getHost(), url.getPath()));
    }

    /**
     * @param url  the url of the resource
     * @param sha1 the sha1 checksum of the resource, or {@code null}
     * @throws java.net.MalformedURLException if url is malformed
     */
    @SuppressWarnings("JavaDoc")
    public CachedResource(@NotNull String url, @Nullable String sha1) {
        this(url(url), sha1);
    }

    @SneakyThrows
    private static URL url(String url) {
        return new URL(url);
    }

    @NotNull
    public URL getUrl() {
        return url;
    }

    @Nullable
    public String getSha1() {
        return sha1;
    }

    @NotNull
    public File getCacheLocation() {
        return file;
    }

    /**
     * Reads the cached file.
     * <p>
     * Performs a validity check and (re-)downloads it if the check was not passed.
     *
     * @return the resource's contents
     */
    @SneakyThrows
    @NotNull
    public InputStream require() {
        if (!validate()) {
            cache();
        }

        return new FileInputStream(file);
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

    /**
     * Removes the cached copy.
     *
     * @return {@code true}, if the local copy was deleted (or never existed in the first place), {@code false} on failure
     */
    public boolean purge() {
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    @SneakyThrows
    private void cache() {
        // download to file
        val dir = file.getParentFile();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new RuntimeException("Cannot create " + dir + " but its necessary for caching " + file);
            }
        }
        IOUtil.write(request(), new FileOutputStream(file));
    }

    @SneakyThrows
    private boolean validate() {
        if (!file.exists())
            return false;
        // if file exists and sha1 sum is not supplied, just assume file is valid
        if (sha1 == null)
            return true;

        val logger = ULib.logger();
        logger.fine(() -> String.format("Checking integrity of file %s", file));
        logger.fine(() -> String.format("Expected SHA-1 sum: %s", sha1));
        logger.fine(() -> "Computing SHA-1 ...");

        String sha1 = ChecksumUtils.getFileChecksum(MessageDigest.getInstance("SHA-1"), file);
        logger.fine(() -> String.format("SHA-1 of %s is %s", file.getName(), sha1));
        boolean valid = sha1.equalsIgnoreCase(this.sha1);

        logger.fine(() -> String.format("File is %s", valid ? "valid" : "corrupted"));
        return valid;
    }

}