package eu.software4you.ulib.core.api.http;

import eu.software4you.ulib.core.api.io.IOUtil;
import eu.software4you.ulib.core.api.util.HashUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.function.Consumer;

import static eu.software4you.ulib.core.ULib.logger;

/**
 * File with validity checks.
 */
@Getter
public class ChecksumFile {
    protected final String algorithm;
    protected final String checksum;
    protected final File file;
    protected final File checksumFile;
    @Getter(AccessLevel.NONE)
    protected final Consumer<File> generate;

    /**
     * @param algorithm    the hash algorithm, using {@link MessageDigest}
     * @param checksum     the checksum, may be {@code null}
     * @param file         the local file
     * @param checksumFile the local file storing the checksum
     * @param generate     function to (re-)generate the file
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull File file, @NotNull File checksumFile, @Nullable Consumer<File> generate) {
        this.algorithm = algorithm;
        this.checksum = checksum;
        this.file = file;
        this.checksumFile = checksumFile;
        this.generate = generate;
    }

    /**
     * When using this constructor the method {@link #generate()} must be overwritten!
     *
     * @param algorithm    the hash algorithm, using {@link MessageDigest}
     * @param checksum     the checksum, may be {@code null}
     * @param file         the local file
     * @param checksumFile the local file storing the checksum
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull File file, @NotNull File checksumFile) {
        this(algorithm, checksum, file, checksumFile, null);
    }

    /**
     * When using this constructor the method {@link #generate()} must be overwritten!
     *
     * @param algorithm the hash algorithm, using {@link MessageDigest}
     * @param checksum  the checksum, may be {@code null}
     * @param root      the root directory in which the files will be placed
     * @param prefix    the file prefix
     * @param file      the path for the file
     * @param generate  function to (re-)generate the file
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull File root, @NotNull String prefix, @NotNull Path file, @Nullable Consumer<File> generate) {
        this.algorithm = algorithm;
        this.checksum = checksum;
        this.file = new File(root, String.format("%s/root/%s", prefix, file));
        String algo = algorithm.toLowerCase().replace("-", "");
        this.checksumFile = new File(root, String.format("%s/checksum/%s.%s", prefix, file, algo));
        this.generate = generate;
    }

    /**
     * When using this constructor the method {@link #generate()} must be overwritten!
     *
     * @param algorithm the hash algorithm, using {@link MessageDigest}
     * @param checksum  the checksum, may be {@code null}
     * @param root      the root directory in which the files will be placed
     * @param prefix    the file prefix
     * @param file      the path for the file
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull File root, @NotNull String prefix, @NotNull Path file) {
        this(algorithm, checksum, root, prefix, file, null);
    }

    /**
     * Removes the local copy.
     *
     * @return {@code true}, if the local copy was deleted (or never existed in the first place), {@code false} on failure
     */
    public boolean purge() {
        if (file.exists()) {
            return file.delete() && (!checksumFile.exists() || checksumFile.delete());
        }
        return true;
    }

    /**
     * Reads the file.
     * <p>
     * Performs a validity check and (re-)generates it if the check was not passed.
     *
     * @return the file's contents
     */
    @SneakyThrows
    @NotNull
    public InputStream require() {
        ensure();
        return new FileInputStream(file);
    }

    /**
     * Generates the file if its invalid.
     */
    public void ensure() {
        if (!validate()) {
            generate();
            genChecksumFile();
        }
    }

    protected void generate() {
        mkdirsp(file);
        Objects.requireNonNull(generate).accept(this.file);
    }

    @SneakyThrows
    protected void genChecksumFile() {
        // save checksum if not supplied
        if (checksum != null) {
            return;
        }
        logger().finer(() -> String.format("Caching sha1 as file of %s", file.getName()));
        mkdirsp(checksumFile);
        IOUtil.write(new ByteArrayInputStream(hash().getBytes()),
                new FileOutputStream(checksumFile));
    }

    @SneakyThrows
    private boolean validate() {
        if (!file.exists())
            return false;
        var logger = logger();
        logger.fine(() -> String.format("Checking integrity of %s", file));

        String expected;
        // if file exists and checksum sum is not supplied, read checksum from file
        if (this.checksum == null) {
            if (!checksumFile.exists()) {
                logger.fine(() -> String.format("No %s for %s found, re-download", algorithm, file.getName()));
                return false; // if checksum not saved, re-download and re-generated sha1File
            }
            var bout = new ByteArrayOutputStream();
            IOUtil.write(new FileInputStream(checksumFile), bout);
            expected = bout.toString();
        } else {
            expected = this.checksum;
        }


        logger.finer(() -> String.format("Expected %s sum: %s", algorithm, expected));

        String checksum = hash();
        logger.finer(() -> String.format("%s of %s is %s", algorithm, file.getName(), checksum));
        boolean valid = checksum.equalsIgnoreCase(expected);

        logger.fine(() -> String.format("%s is %s", file.getName(), valid ? "valid" : "corrupted"));
        return valid;
    }

    @SneakyThrows
    private String hash() {
        return HashUtil.computeHex(new FileInputStream(file), MessageDigest.getInstance(algorithm));
    }

    protected void mkdirsp(File child) {
        var dir = child.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
