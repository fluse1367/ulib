package eu.software4you.http;

import eu.software4you.ulib.ULib;
import eu.software4you.utils.ChecksumUtils;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;

import static eu.software4you.ulib.ULib.logger;

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
    private final File sha1File;

    /**
     * @param url    the url of the resource
     * @param sha1   the sha1 checksum of the resource, or {@code null}
     * @param prefix prefix of local cache location path
     */
    public CachedResource(@NotNull String url, @Nullable String sha1, @NotNull String prefix) {
        this.url = url(url);
        this.sha1 = sha1;
        String loc = this.url.getHost() + this.url.getPath();
        this.file = new File(ULib.get().getCacheDir(), String.format("%s/%s", prefix, loc));
        this.sha1File = new File(ULib.get().getCacheDir(), String.format("%s_hash/%s.sha1", prefix, loc));
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
            return file.delete() && (!sha1File.exists() || sha1File.delete());
        }
        return true;
    }

    @SneakyThrows
    private void cache() {
        logger().finer(() -> String.format("Caching %s", file.getName()));

        // download to file
        mkdirsp(file);
        IOUtil.write(request(), new FileOutputStream(file));

        // save sha1 if not supplied
        if (sha1 == null) {
            logger().finer(() -> String.format("Caching sha1 as file of %s", file.getName()));
            mkdirsp(sha1File);
            IOUtil.write(new ByteArrayInputStream(genSha1(file).getBytes()),
                    new FileOutputStream(sha1File));
        }
    }

    @SneakyThrows
    private boolean validate() {
        if (!file.exists())
            return false;
        val logger = logger();
        logger.fine(() -> String.format("Checking integrity of %s", file));

        String expected;
        // if file exists and sha1 sum is not supplied, read sha1 from file
        if (this.sha1 == null) {
            if (!sha1File.exists()) {
                logger.fine(() -> String.format("No sha1 for %s found, re-download", file.getName()));
                return false; // if sha1 not saved, re-download and re-generated sha1File
            }
            val bout = new ByteArrayOutputStream();
            IOUtil.write(new FileInputStream(sha1File), bout);
            expected = bout.toString();
        } else {
            expected = this.sha1;
        }


        logger.finer(() -> String.format("Expected SHA-1 sum: %s", expected));

        String sha1 = genSha1(file);
        logger.finer(() -> String.format("SHA-1 of %s is %s", file.getName(), sha1));
        boolean valid = sha1.equalsIgnoreCase(expected);

        logger.fine(() -> String.format("%s is %s", file.getName(), valid ? "valid" : "corrupted"));
        return valid;
    }

    private void mkdirsp(File child) {
        val dir = child.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    @SneakyThrows
    private String genSha1(File file) {
        return ChecksumUtils.getFileChecksum(MessageDigest.getInstance("SHA-1"), file);
    }

}
