package eu.software4you.ulib.core.configuration;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Represents a generic configuration that can be re-initialized with different data.
 */
public interface ConfigurationReinit extends Configuration {
    /**
     * Clears all data from the sub and loads in new data read from a reader.
     *
     * @param reader the data
     */
    @NotNull
    Expect<Void, IOException> reinit(@NotNull Reader reader);

    /**
     * Clears all data from the sub and loads in new data read from a stream.
     *
     * @param in the data stream
     * @return the result as {@link Expect} object
     */
    @NotNull
    default Expect<Void, IOException> reinit(@NotNull InputStream in) {
        return reinit(new InputStreamReader(in));
    }

    /**
     * Clears all data from the sub and loads in new data read from a path.
     *
     * @param path the path to read the data from
     * @return the result as {@link Expect} object
     */
    @NotNull
    default Expect<Void, IOException> reinitFrom(@NotNull Path path) {
        return Expect.compute(() -> {
            try (var reader = Files.newBufferedReader(path)) {
                reinit(reader).rethrow(IOException.class);
            }
        });
    }

    /**
     * Writes this sub to a writer.
     *
     * @param writer the writer to write to
     * @return the result as {@link Expect} object
     */
    @NotNull
    Expect<Void, IOException> dump(@NotNull Writer writer);

    /**
     * Writes this sub to a stream.
     *
     * @param out the stream to write to
     * @return the result as {@link Expect} object
     */
    @NotNull
    default Expect<Void, IOException> dump(@NotNull OutputStream out) {
        return dump(new OutputStreamWriter(out));
    }

    /**
     * Writes this sub to a path.
     *
     * @param path the path to write to
     * @return the result as {@link Expect} object
     */
    @NotNull
    default Expect<Void, IOException> dumpTo(@NotNull Path path) {
        return Expect.compute(() -> {
            try (var writer = Files.newBufferedWriter(path)) {
                dump(writer).rethrow(IOException.class);
            }
        });
    }

    /**
     * Clears all data from this sub.
     */
    void clear();
}
