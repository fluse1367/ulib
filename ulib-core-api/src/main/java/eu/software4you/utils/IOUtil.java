package eu.software4you.utils;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class IOUtil {
    /**
     * Writes an input stream into an output stream. This method closes the streams.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @throws IOException inherited from {@link InputStream#read()}, {@link InputStream#close()},
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
     * Writes an reader into a writer. This method closes the objects.
     *
     * @param reader the reader to read from
     * @param writer the writer to write to
     * @throws IOException inherited from {@link Reader#read()}, {@link Reader#close()},
     *                     {@link Writer#write(char[], int, int)}, {@link Writer#flush()} and {@link Writer#close()}
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    public static void write(@NotNull Reader reader, @NotNull Writer writer) throws IOException {
        try (val in = reader; val out = writer) {
            char[] buff = new char[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.flush();
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

    /**
     * Reads all chars from an reader.
     *
     * @param reader the reader to read from
     * @return the bytes read
     * @throws IOException inherited from {@link #write(Reader, Writer)}
     * @see #write(InputStream, OutputStream)
     */
    public static char[] read(@NotNull Reader reader) throws IOException {
        val cout = new CharArrayWriter();
        write(reader, cout);
        return cout.toCharArray();
    }

    /**
     * Reads all bytes from an input stream into a string. This method closes the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @throws IOException inherited from {@link #write(InputStream, OutputStream)}
     * @see #write(InputStream, OutputStream)
     */
    public static String toString(@NotNull InputStream in) throws IOException {
        val bout = new ByteArrayOutputStream();
        write(in, bout);
        return bout.toString();
    }
}
