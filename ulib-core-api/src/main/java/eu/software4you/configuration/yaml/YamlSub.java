package eu.software4you.configuration.yaml;

import eu.software4you.configuration.Sub;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;

/**
 * Representation of a YAML-type configuration sub.
 */
public interface YamlSub extends Sub {

    /**
     * Returns this sub's root YAML node.
     * <p>
     * Not to be confused with {@code getRoot().asNode()}!
     * <p>
     * <b>Warning:</b> Changes may not be reflected back to this sub.
     *
     * @return the yaml node
     */
    @NotNull
    Node asNode();

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
    @NotNull Collection<YamlSub> getSubs();
}
