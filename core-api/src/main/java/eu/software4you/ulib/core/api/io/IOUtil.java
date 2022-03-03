package eu.software4you.ulib.core.api.io;

import eu.software4you.ulib.core.api.util.value.Unsettled;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.ThreadFactory;
import java.util.function.Supplier;

/**
 * Class containing I/O (stream) operations.
 */
public class IOUtil {
    /**
     * Writes an input stream into an output stream. This method does not close the streams.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @implNote this method may throw an {@link IOException}, consider using it in conjunction with {@link Unsettled#execute(Supplier)}
     * @see #redirect(InputStream, OutputStream)
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    @SneakyThrows
    public static void write(@NotNull InputStream in, @NotNull OutputStream out) {
        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        out.flush();
    }

    /**
     * Writes a reader into a writer. This method closes the objects.
     *
     * @param in  the reader to read from
     * @param out the writer to write to
     * @implNote this method may throw an {@link IOException}, consider using it in conjunction with {@link Unsettled#execute(Supplier)}
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    @SneakyThrows
    public static void write(@NotNull Reader in, @NotNull Writer out) {
        char[] buff = new char[1024];
        int len;
        while ((len = in.read(buff)) != -1) {
            out.write(buff, 0, len);
        }
        out.flush();
    }

    /**
     * Reads all bytes from an input stream. This method does not close the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @implNote this method may throw an {@link IOException}, consider using it in conjunction with {@link Unsettled#execute(Supplier)}
     * @see #write(InputStream, OutputStream)
     */
    @SneakyThrows
    public static byte[] read(@NotNull InputStream in) {
        try (var bout = new ByteArrayOutputStream()) {
            write(in, bout);
            return bout.toByteArray();
        }
    }

    /**
     * Reads all chars from a reader.
     *
     * @param reader the reader to read from
     * @return the bytes read
     * @implNote this method may throw an {@link IOException}, consider using it in conjunction with {@link Unsettled#execute(Supplier)}
     * @see #write(InputStream, OutputStream)
     */
    public static char[] read(@NotNull Reader reader) {
        try (var cout = new CharArrayWriter()) {
            write(reader, cout);
            return cout.toCharArray();
        }
    }

    /**
     * Reads all bytes from an input stream into a string. This method does not close the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @implNote this method may throw an {@link IOException}, consider using it in conjunction with {@link Unsettled#execute(Supplier)}
     * @see #write(InputStream, OutputStream)
     */
    @SneakyThrows
    public static String toString(@NotNull InputStream in) {
        try (var bout = new ByteArrayOutputStream()) {
            write(in, bout);
            return bout.toString();
        }
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
        return factory.newThread(prepareRedirect(in, out));
    }

    /**
     * Creates a new {@link Runnable} that redirects all data read from an {@link InputStream} to an {@link OutputStream}.
     * Any exception thrown by {@link InputStream#read()} will not be caught.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @return the runnable
     * @see #write(InputStream, OutputStream)
     */
    public static Runnable prepareRedirect(InputStream in, OutputStream out) {
        //noinspection Convert2Lambda
        return new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                int b;
                while (!Thread.currentThread().isInterrupted() && (b = in.read()) != -1) {
                    out.write(b);
                }
            }
        };
    }
}
