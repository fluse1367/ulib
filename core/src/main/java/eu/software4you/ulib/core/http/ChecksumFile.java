package eu.software4you.ulib.core.http;

import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.Expect;
import eu.software4you.ulib.core.util.HashUtil;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.file.Files;
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
    protected final Path fileLocation;
    @NotNull
    protected final Path checksumFileLocation;
    @Getter(AccessLevel.NONE)
    protected final Consumer<Path> generate;

    /**
     * @param algorithm            the hash algorithm, using {@link MessageDigest}
     * @param checksum             the checksum, may be {@code null}
     * @param fileLocation         the local file
     * @param checksumFileLocation the local file storing the checksum
     * @param generate             function to (re-)generate the file
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull Path fileLocation,
                        @NotNull Path checksumFileLocation, @Nullable Consumer<Path> generate) {
        this.algorithm = algorithm;
        this.checksum = checksum;
        this.fileLocation = fileLocation;
        this.checksumFileLocation = checksumFileLocation;
        this.generate = generate;
    }

    /**
     * When using this constructor the method {@link #generate()} must be overwritten!
     *
     * @param algorithm            the hash algorithm, using {@link MessageDigest}
     * @param checksum             the checksum, may be {@code null}
     * @param fileLocation         the local file
     * @param checksumFileLocation the local file storing the checksum
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull Path fileLocation, @NotNull Path checksumFileLocation) {
        this(algorithm, checksum, fileLocation, checksumFileLocation, null);
    }

    /**
     * When using this constructor the method {@link #generate()} must be overwritten!
     *
     * @param algorithm    the hash algorithm, using {@link MessageDigest}
     * @param checksum     the checksum, may be {@code null}
     * @param root         the root directory in which the files will be placed
     * @param prefix       the file prefix
     * @param fileLocation the path for the file
     * @param generate     function to (re-)generate the file
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull Path root, @NotNull String prefix,
                        @NotNull Path fileLocation, @Nullable Consumer<Path> generate) {
        this.algorithm = algorithm;
        this.checksum = checksum;
        this.fileLocation = root.resolve(Path.of(prefix, "root")).resolve(fileLocation);
        String algo = algorithm.toLowerCase().replace("-", "");
        this.checksumFileLocation = root.resolve(Path.of(prefix, "checksum"))
                .resolve(fileLocation.getParent()).resolve(fileLocation.getFileName().toString() + "." + algo);
        this.generate = generate;
    }

    /**
     * When using this constructor the method {@link #generate()} must be overwritten!
     *
     * @param algorithm    the hash algorithm, using {@link MessageDigest}
     * @param checksum     the checksum, may be {@code null}
     * @param root         the root directory in which the files will be placed
     * @param prefix       the file prefix
     * @param fileLocation the path for the file
     */
    public ChecksumFile(@NotNull String algorithm, @Nullable String checksum, @NotNull Path root, @NotNull String prefix, @NotNull Path fileLocation) {
        this(algorithm, checksum, root, prefix, fileLocation, null);
    }

    /**
     * Removes the local copy.
     *
     * @return {@code true}, if the local copy was deleted (or never existed in the first place), {@code false} on failure
     */
    public boolean purge() {
        if (Files.exists(fileLocation)) {
            return !Expect.compute(() -> Files.delete(fileLocation)).hasCaught()
                   && (!Files.exists(checksumFileLocation) || !Expect.compute(() -> Files.delete(checksumFileLocation)).hasCaught());
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
    public Expect<InputStream, IOException> require() {
        ensure();
        return Expect.compute(() -> Files.newInputStream(fileLocation));
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
        Expect.compute(() -> Files.createDirectories(fileLocation.getParent())).rethrowRE();
        Objects.requireNonNull(generate).accept(this.fileLocation);
    }

    @SneakyThrows
    protected void genChecksumFile() {
        // save checksum if not supplied
        if (checksum != null) {
            return;
        }
        Files.createDirectories(checksumFileLocation.getParent());
        try (var in = new ByteArrayInputStream(hash().getBytes());
             var out = Files.newOutputStream(checksumFileLocation)) {
            IOUtil.write(in, out).rethrow();
        }
    }

    @SneakyThrows
    private boolean validate() {
        if (!Files.exists(fileLocation))
            return false;

        String expected;
        // if file exists and checksum sum is not supplied, read checksum from file
        if (this.checksum == null) {
            if (!Files.exists(checksumFileLocation)) {
                return false; // if checksum not saved, re-download and re-generated sha1File
            }
            var bout = new ByteArrayOutputStream();
            try (var in = Files.newInputStream(checksumFileLocation)) {
                IOUtil.write(in, bout).rethrow();
            }
            expected = bout.toString();
        } else {
            expected = this.checksum;
        }

        return hash().equalsIgnoreCase(expected);
    }

    @SneakyThrows
    private String hash() {
        try (var in = Files.newInputStream(fileLocation)) {
            return HashUtil.computeHex(in, MessageDigest.getInstance(algorithm))
                    .orElseRethrow();
        }
    }
}
