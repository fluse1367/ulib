package eu.software4you.ulib.core.configuration;

import eu.software4you.ulib.core.impl.configuration.json.JsonSerializer;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.Collection;
import java.util.Optional;

/**
 * Representation of a JSON-Type Configuration sub.
 */
public interface JsonConfiguration extends ConfigurationReinit {


    /**
     * Creates a new empty JSON-Typed {@link Configuration}.
     *
     * @return the newly created sub
     */
    static JsonConfiguration newJson() {
        return JsonSerializer.getInstance().createNew();
    }

    /**
     * Loads a JSON-Typed {@link Configuration} from a reader.
     *
     * @param reader the JSON document
     * @return the loaded sub
     */
    @NotNull
    static Expect<JsonConfiguration, IOException> loadJson(@NotNull Reader reader) {
        return Expect.compute(() -> {
            try (reader) {
                return JsonSerializer.getInstance().deserialize(reader);
            }
        });
    }

    /**
     * Loads a JSON-Typed {@link Configuration} from a stream.
     *
     * @param in the JSON document
     * @return the loaded sub
     */
    @NotNull
    static Expect<JsonConfiguration, IOException> loadJson(@NotNull InputStream in) {
        return loadJson(new InputStreamReader(in));
    }

    /**
     * Loads a JSON-Typed {@link Configuration} from a file.
     *
     * @param file the JSON document
     * @return the loaded sub
     */
    @NotNull
    static Expect<JsonConfiguration, IOException> loadJson(@NotNull File file) {
        return Expect.compute(() -> loadJson(new FileInputStream(file)).orElseRethrow());
    }

    @Override
    @NotNull Optional<? extends JsonConfiguration> getSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends JsonConfiguration> getSubs();

    @Override
    @NotNull JsonConfiguration createSub(@NotNull String path);

    @Override
    @NotNull JsonConfiguration getRoot();

    @Override
    @NotNull JsonConfiguration subAndCreate(@NotNull String path);
}
