package eu.software4you.ulib.minecraft.launchermeta;

import eu.software4you.ulib.core.api.io.IOUtil;
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
     * @throws java.io.FileNotFoundException if the file could not be found or created
     * @throws IOException                   if an IO error occurs
     */
    default void download(File dest) throws IOException {
        download(new FileOutputStream(dest));
    }

    /**
     * Writes the contents of the file into the stream.
     *
     * @param out the stream to write to
     * @throws IOException if an IO error occurs
     */
    default void download(OutputStream out) throws IOException {
        IOUtil.write(download(), out);
    }

    /**
     * Downloads teh contents of the file.
     *
     * @return the content stream
     */
    InputStream download();
}
