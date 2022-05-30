package eu.software4you.ulib.core.configuration;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Represents a generic configuration that can be re-initialized with different data.
 */
public interface ConfigurationReinit extends Configuration {
    /**
     * Clears all data from the sub and loads new data in.
     *
     * @param reader the data
     */
    @NotNull
    Expect<Void, IOException> reinit(@NotNull Reader reader);

    /**
     * Writes this sub to a writer.
     *
     * @param writer the writer to write to
     */
    @NotNull
    Expect<Void, IOException> dump(@NotNull Writer writer);

    /**
     * Clears all data from this sub.
     */
    void clear();
}
