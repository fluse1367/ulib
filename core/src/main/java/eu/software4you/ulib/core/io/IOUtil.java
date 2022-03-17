package eu.software4you.ulib.core.io;

import eu.software4you.ulib.core.function.Task;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.ThreadFactory;

/**
 * Class containing I/O (stream) operations.
 */
public class IOUtil {
    /**
     * Writes an input stream into an output stream. This method does not close the streams.
     *
     * @param in  the stream to read from
     * @param out the stream to write to
     * @see #redirect(InputStream, OutputStream)
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    public static Expect<Void, IOException> write(@NotNull InputStream in, @NotNull OutputStream out) {
        return Expect.compute(() -> {
            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.flush();
        });
    }

    /**
     * Writes a reader into a writer. This method closes the objects.
     *
     * @param in  the reader to read from
     * @param out the writer to write to
     * @see InputStream#read()
     * @see InputStream#close()
     * @see OutputStream#write(byte[], int, int)
     * @see OutputStream#flush()
     * @see OutputStream#close()
     */
    public static Expect<Void, IOException> write(@NotNull Reader in, @NotNull Writer out) {
        return Expect.compute(() -> {
            char[] buff = new char[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                out.write(buff, 0, len);
            }
            out.flush();
        });
    }

    /**
     * Reads all bytes from an input stream. This method does not close the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @see #write(InputStream, OutputStream)
     */
    public static Expect<byte[], IOException> read(@NotNull InputStream in) {
        return Expect.compute(() -> {
            try (var bout = new ByteArrayOutputStream()) {
                write(in, bout).rethrow();
                return bout.toByteArray();
            }
        });
    }

    /**
     * Reads all chars from a reader.
     *
     * @param reader the reader to read from
     * @return the bytes read
     * @see #write(InputStream, OutputStream)
     */
    public static Expect<char[], IOException> read(@NotNull Reader reader) {
        return Expect.compute(() -> {
            try (var cout = new CharArrayWriter()) {
                write(reader, cout).rethrow();
                return cout.toCharArray();
            }
        });
    }

    /**
     * Reads all bytes from an input stream into a string. This method does not close the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @see #write(InputStream, OutputStream)
     */
    public static Expect<String, IOException> toString(@NotNull InputStream in) {
        return Expect.compute(() -> {
            try (var bout = new ByteArrayOutputStream()) {
                write(in, bout).rethrow();
                return bout.toString();
            }
        });
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
    public static Task<IOException> prepareRedirect(InputStream in, OutputStream out) {
        return () -> {
            int b;
            while (!Thread.currentThread().isInterrupted() && (b = in.read()) != -1) {
                out.write(b);
            }
        };
    }
}
