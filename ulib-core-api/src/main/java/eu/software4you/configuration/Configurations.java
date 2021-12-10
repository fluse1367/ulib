package eu.software4you.ulib.core.api.configuration;

import eu.software4you.ulib.core.api.configuration.yaml.YamlSub;
import eu.software4you.ulib.Await;
import org.jetbrains.annotations.NotNull;

import java.io.*;

/**
 * Class for loading/deserializing and saving/serializing configuration-subs.
 */
public abstract class Configurations {
    @Await
    private static Configurations impl;

    /* YAML */

    /**
     * Creates a new empty {@link YamlSub}.
     *
     * @return the newly created sub
     */
    @NotNull
    public static YamlSub newYaml() {
        return impl.newYaml0();
    }

    /**
     * Loads a {@link YamlSub} from a file.
     *
     * @param file the YAML document
     * @return the loaded sub
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException see {@link FileInputStream#FileInputStream(File) FileInputStream(File)}
     */
    @NotNull
    public static YamlSub loadYaml(@NotNull File file) throws IOException {
        return loadYaml(new FileInputStream(file));
    }

    /**
     * Loads a {@link YamlSub} from a stream.
     *
     * @param in the YAML document
     * @return the loaded sub
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    public static YamlSub loadYaml(@NotNull InputStream in) throws IOException {
        return loadYaml(new InputStreamReader(in));
    }

    /**
     * Loads a {@link YamlSub} from a reader.
     *
     * @param reader the YAML document
     * @return the loaded sub
     * @throws IOException If an I/O error occurs
     */
    @NotNull
    public static YamlSub loadYaml(@NotNull Reader reader) throws IOException {
        return impl.loadYaml0(reader);
    }

    /**
     * Serializes a {@link YamlSub}.
     *
     * @param yaml the YAML document
     * @return a stream containing the yaml document
     * @throws IOException If an I/O error occurs
     */
    public static InputStream dumpYaml(YamlSub yaml) throws IOException {
        var out = new ByteArrayOutputStream();
        saveYaml(yaml, out);
        return new ByteArrayInputStream(out.toByteArray());
    }

    /**
     * Writes (serializes) a {@link YamlSub} to a file.
     *
     * @param yaml the YAML document
     * @param file the file to write to
     * @throws IOException           If an I/O error occurs
     * @throws FileNotFoundException see {@link FileOutputStream#FileOutputStream(File) FileOutputStream(File)}
     */
    public static void saveYaml(@NotNull YamlSub yaml, @NotNull File file) throws IOException {
        saveYaml(yaml, new FileOutputStream(file));
    }

    /**
     * Writes (serializes) a {@link YamlSub} to a stream.
     *
     * @param yaml the YAML document
     * @param out  the stream to write to
     * @throws IOException If an I/O error occurs
     */
    public static void saveYaml(@NotNull YamlSub yaml, @NotNull OutputStream out) throws IOException {
        saveYaml(yaml, new OutputStreamWriter(out));
    }

    /**
     * Writes (serializes) a {@link YamlSub} to a writer.
     *
     * @param yaml   the YAML document
     * @param writer the writer to write to
     * @throws IOException If an I/O error occurs
     */
    public static void saveYaml(@NotNull YamlSub yaml, @NotNull Writer writer) throws IOException {
        impl.saveYaml0(yaml, writer);
    }

    protected abstract YamlSub newYaml0();

    protected abstract YamlSub loadYaml0(Reader reader) throws IOException;

    protected abstract void saveYaml0(YamlSub yaml, Writer writer) throws IOException;
}
