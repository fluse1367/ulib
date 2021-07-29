package eu.software4you.io;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.ThreadFactory;

/**
 * Class containing I/O (stream) operations.
 */
public class IOUtil {
    /**
     * Writes an input stream into an output stream. This method closes the streams.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @throws IOException inherited from {@link InputStream#read()}, {@link InputStream#close()},
     *                     {@link OutputStream#write(byte[], int, int)}, {@link OutputStream#flush()} and {@link OutputStream#close()}
     * @see #redirect(InputStream, OutputStream)
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    public static void write(@NotNull InputStream in, @NotNull OutputStream out) throws IOException {
        try (var is = in; var os = out) {
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
        try (var in = reader; var out = writer) {
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
        var bout = new ByteArrayOutputStream();
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
        var cout = new CharArrayWriter();
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
        var bout = new ByteArrayOutputStream();
        write(in, bout);
        return bout.toString();
    }

    /**
     * Creates a new {@link Thread} that redirects all data read from an {@link InputStream} to an {@link OutputStream}.
     * <p>
     * The {@link Thread} will not be started by this method.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @return the thread
     * @see #write(InputStream, OutputStream)
     */
    public static Thread redirect(InputStream in, OutputStream out) {
        return redirect(in, out, Thread::new);
    }

    /**
     * Creates a new {@link Thread} that redirects all data read from an {@link InputStream} to an {@link OutputStream}.
     * Any exception thrown by {@link InputStream#read()} will not be caught.
     * <p>
     * The {@link Thread} will not be started by this method.
     *
     * @param in      the stream to read from
     * @param out     the stream to write to
     * @param factory the factory to create the thread
     * @return the thread
     * @see #write(InputStream, OutputStream)
     */
    public static Thread redirect(InputStream in, OutputStream out, ThreadFactory factory) {
        //noinspection Convert2Lambda
        return factory.newThread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                int b;
                while (!Thread.currentThread().isInterrupted() && (b = in.read()) != -1) {
                    out.write(b);
                }
            }
        });
    }
}
