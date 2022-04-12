package eu.software4you.ulib.minecraft.launchermeta;

import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.URL;

/**
 * Represents a downloadable mojang-resource.
 */
public interface RemoteResource {
    /**
     * Returns the resource's id
     *
     * @return the id
     */
    @NotNull
    String getId();

    /**
     * Returns the SHA-1 file hash
     *
     * @return the file hash
     */
    @NotNull
    String getSha1();

    /**
     * Returns the file size.
     *
     * @return the file size
     */
    long getSize();

    /**
     * Returns the representing file url.
     *
     * @return the representing file url
     */
    @NotNull
    URL getUrl();

    /**
     * Downloads the file.
     *
     * @param dest the destination
     */
    default Expect<Void, IOException> download(File dest) {
        return Expect.compute(() -> download(new FileOutputStream(dest)).rethrow());
    }

    /**
     * Writes the contents of the file into the stream.
     *
     * @param out the stream to write to
     */
    default Expect<Void, IOException> download(OutputStream out) {
        return Expect.compute(() -> {
            try (var in = download().orElseRethrow()) {
                IOUtil.write(in, out).rethrow();
            }
        });
    }

    /**
     * Downloads teh contents of the file.
     *
     * @return the content stream
     */
    Expect<InputStream, IOException> download();
}
