package eu.software4you.ulib.core.configuration;

import eu.software4you.ulib.core.impl.configuration.yaml.YamlSerializer;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

/**
 * Representation of a YAML-type configuration sub.
 */
public interface YamlConfiguration extends ConfigurationReinit {
    /**
     * Creates a new empty YAML-Typed {@link Configuration}.
     *
     * @return the newly created sub
     */
    static YamlConfiguration newYaml() {
        return YamlSerializer.getInstance().createNew();
    }

    /**
     * Loads a YAML-Typed {@link Configuration} from a reader.
     *
     * @param reader the YAML document
     * @return the loaded sub
     */
    @NotNull
    static Expect<YamlConfiguration, IOException> loadYaml(@NotNull Reader reader) {
        return Expect.compute(() -> {
            try (reader) {
                return YamlSerializer.getInstance().deserialize(reader);
            }
        });
    }

    /**
     * Loads a YAML-Typed {@link Configuration} from a stream.
     *
     * @param in the YAML document
     * @return the loaded sub
     */
    @NotNull
    static Expect<YamlConfiguration, IOException> loadYaml(@NotNull InputStream in) {
        return loadYaml(new InputStreamReader(in));
    }

    /**
     * Loads a YAML-Typed {@link Configuration} from a file.
     *
     * @param file the YAML document
     * @return the loaded sub
     */
    @NotNull
    static Expect<YamlConfiguration, IOException> loadYaml(@NotNull File file) {
        return Expect.compute(() -> loadYaml(new FileInputStream(file)).orElseRethrow());
    }


    /**
     * Returns the comments of a specific key.
     *
     * @param path the key path; elements seperated by {@code .}
     * @return a list with the comment lines, or {@code null} if {@code path} not found
     */
    @Nullable
    List<String> getComments(@NotNull String path);

    /**
     * Sets the comments of a specific key.
     *
     * @param path  the key path; elements seperated by {@code .}
     * @param lines the comment lines to set
     * @throws IllegalArgumentException if path is not found
     */
    void setComments(@NotNull String path, String... lines) throws IllegalArgumentException;

    /**
     * Sets the comments of a specific key.
     *
     * @param path  the key path; elements seperated by {@code .}
     * @param lines the comment lines to set
     * @throws IllegalArgumentException if path is not found
     */
    default void setComments(@NotNull String path, @NotNull List<String> lines) throws IllegalArgumentException {
        setComments(path, lines.toArray(new String[0]));
    }

    @Override
    @NotNull Optional<? extends YamlConfiguration> getSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends YamlConfiguration> getSubs();

    @Override
    @NotNull YamlConfiguration createSub(@NotNull String path);

    @Override
    @NotNull YamlConfiguration getRoot();

    @Override
    @NotNull YamlConfiguration subAndCreate(@NotNull String path);
}
