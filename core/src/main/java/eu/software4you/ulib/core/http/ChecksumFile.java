package eu.software4you.ulib.core.http;

import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.Expect;
import eu.software4you.ulib.core.util.HashUtil;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * File with validity checks.
 */
// TODO: replace File with Path
@Getter
public class ChecksumFile {
    @NotNull
    protected final String algorithm;
    @Nullable
    protected final String checksum;
    @NotNull
    protected final File file;
    @NotNull
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
    @NotNull
    public Expect<InputStream, FileNotFoundException> require() {
        ensure();
        return Expect.compute(() -> new FileInputStream(file));
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
        mkdirsp(checksumFile);
        try (var in = new ByteArrayInputStream(hash().getBytes()); var out = new FileOutputStream(checksumFile)) {
            IOUtil.write(in, out);
        }
    }

    @SneakyThrows
    private boolean validate() {
        if (!file.exists())
            return false;

        String expected;
        // if file exists and checksum sum is not supplied, read checksum from file
        if (this.checksum == null) {
            if (!checksumFile.exists()) {
                return false; // if checksum not saved, re-download and re-generated sha1File
            }
            var bout = new ByteArrayOutputStream();
            try (var in = new FileInputStream(checksumFile)) {
                IOUtil.write(in, bout);
            }
            expected = bout.toString();
        } else {
            expected = this.checksum;
        }

        return hash().equalsIgnoreCase(expected);
    }

    @SneakyThrows
    private String hash() {
        try (var in = new FileInputStream(file)) {
            return HashUtil.computeHex(in, MessageDigest.getInstance(algorithm))
                    .orElseRethrow();
        }
    }

    protected void mkdirsp(File child) {
        var dir = child.getParentFile();
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
