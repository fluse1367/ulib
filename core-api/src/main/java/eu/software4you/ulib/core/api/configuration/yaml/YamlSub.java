package eu.software4you.ulib.core.api.configuration.yaml;

import eu.software4you.ulib.core.api.configuration.Sub;
import eu.software4you.ulib.core.api.internal.Providers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Collection;
import java.util.List;

/**
 * Representation of a YAML-type configuration sub.
 */
public interface YamlSub extends Sub {
    /**
     * Creates a new empty YAML-Typed {@link Sub}.
     *
     * @return the newly created sub
     */
    static ExtYamlSub newYaml() {
        return Providers.get(Providers.ProviderExtYamlSub.class).get();
    }


    /**
     * Loads a YAML-Typed {@link Sub} from a reader.
     *
     * @param reader the YAML document
     * @return the loaded sub
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    static ExtYamlSub loadYaml(@NotNull Reader reader) throws IOException {
        var yaml = newYaml();
        yaml.load(reader);
        return yaml;
    }

    /**
     * Loads a YAML-Typed {@link Sub} from a stream.
     *
     * @param in the YAML document
     * @return the loaded sub
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    static ExtYamlSub loadYaml(@NotNull InputStream in) throws IOException {
        return loadYaml(new InputStreamReader(in));
    }

    /**
     * Loads a YAML-Typed {@link Sub} from a file.
     *
     * @param file the YAML document
     * @return the loaded sub
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException see {@link FileInputStream#FileInputStream(File) FileInputStream(File)}
     */
    @NotNull
    static ExtYamlSub loadYaml(@NotNull File file) throws IOException {
        return loadYaml(new FileInputStream(file));
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

    /**
     * Clears all data from the sub and loads new data in.
     *
     * @param reader the data
     * @throws IOException If an I/O error occurs
     */
    void load(Reader reader) throws IOException;

    /**
     * Writes (serializes) this sub to a writer.
     *
     * @param writer the writer to write to
     * @throws IOException If an I/O error occurs
     */
    void save(Writer writer) throws IOException;

    @Override
    @Nullable YamlSub getSub(@NotNull String path);

    @Override
    @NotNull Collection<? extends YamlSub> getSubs();

    @Override
    @NotNull YamlSub createSub(@NotNull String path);
}
