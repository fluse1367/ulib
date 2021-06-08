package eu.software4you.utils;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {
    /**
     * Writes an input stream into an output stream. This method closes the streams.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @throws IOException inherited from {@link InputStream#read()}, {@link InputStream#close()} ()},
     *                     {@link OutputStream#write(byte[], int, int)}, {@link OutputStream#flush()} and {@link OutputStream#close()}
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    public static void write(@NotNull InputStream in, @NotNull OutputStream out) throws IOException {
        try (val is = in; val os = out) {
            byte[] buff = new byte[1024];
            int len;
            while ((len = is.read(buff)) != -1) {
                os.write(buff, 0, len);
            }
            os.flush();
        }
    }

    /**
     * Reads all bytes from an input stream. This method closes the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @throws IOException inherited from {@link #write(InputStream, OutputStream)}
     * @see #write(InputStream, OutputStream)
     */
    public static byte[] read(@NotNull InputStream in) throws IOException {
        val bout = new ByteArrayOutputStream();
        write(in, bout);
        return bout.toByteArray();
    }
}
