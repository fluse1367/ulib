package eu.software4you.ulib.core.io;

import eu.software4you.ulib.core.function.*;
import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.concurrent.ThreadFactory;

/**
 * Class containing I/O (stream) operations.
 */
public class IOUtil {

    /**
     * Convenience method to pass updating methods as reference into a blockwise read operation.
     * <p>
     * Reads all available bytes from an input stream in blocks of 1024 and calls a consumer with the respective buffer.
     *
     * @param in       the stream to read from
     * @param consumer the consumer, called with the buffer, an offset (which is always {@code 0} and the read length
     */
    @NotNull
    public static Expect<Void, IOException> updateBlockwise(@NotNull InputStream in,
                                                            @NotNull TriParamTask<byte[], Integer, Integer, ? extends IOException> consumer) {
        return updateBlockwise(1024, in, consumer);
    }

    /**
     * Reads all available bytes from an input stream in blocks of 1024 and calls a consumer with the respective buffer.
     *
     * @param in       the stream to read from
     * @param consumer the consumer, called with the buffer and the read length
     */
    @NotNull
    public static Expect<Void, IOException> readBlockwise(@NotNull InputStream in,
                                                          @NotNull BiParamTask<byte[], Integer, ? extends IOException> consumer) {
        return readBlockwise(1024, in, consumer);
    }

    /**
     * Convenience method to pass updating methods as reference into a blockwise read operation.
     * <p>
     * Reads all available bytes from an input stream in blocks of a certain size and calls a consumer with the respective buffer.
     *
     * @param blockSize the block size
     * @param in        the stream to read from
     * @param consumer  the consumer, called with the buffer, an offset (which is always {@code 0} and the read length
     */
    @NotNull
    public static Expect<Void, IOException> updateBlockwise(int blockSize, @NotNull InputStream in,
                                                            @NotNull TriParamTask<byte[], Integer, Integer, ? extends IOException> consumer) {
        return readBlockwise(blockSize, in, (buf, len) -> consumer.execute(buf, 0, len));
    }


    /**
     * Reads all available bytes from an input stream in blocks of a certain size and calls a consumer with the respective buffer.
     *
     * @param blockSize the block size
     * @param in        the stream to read from
     * @param consumer  the consumer, called with the buffer and the read length
     */
    @NotNull
    public static Expect<Void, IOException> readBlockwise(int blockSize, @NotNull InputStream in,
                                                          @NotNull BiParamTask<byte[], Integer, ? extends IOException> consumer) {
        if (blockSize <= 0)
            throw new IllegalArgumentException("Invalid blocksize");

        return Expect.compute(() -> {
            byte[] buf = new byte[blockSize];
            int len;
            while ((len = in.read(buf)) != -1) {
                consumer.execute(buf, len);
            }
        });
    }


    /**
     * Convenience method to pass updating methods as reference into a blockwise read operation.
     * <p>
     * Reads all available characters from a reader in blocks of 1024 and calls a consumer with the respective buffer.
     *
     * @param in       the stream to read from
     * @param consumer the consumer, called with the buffer, an offset (which is always {@code 0} and the read length
     */
    @NotNull
    public static Expect<Void, IOException> updateBlockwise(@NotNull Reader in,
                                                            @NotNull TriParamTask<char[], Integer, Integer, ? extends IOException> consumer) {
        return updateBlockwise(1024, in, consumer);
    }

    /**
     * Reads all available characters from a reader in blocks of 1024 and calls a consumer with the respective buffer.
     *
     * @param in       the stream to read from
     * @param consumer the consumer, called with the buffer and the read length
     */
    @NotNull
    public static Expect<Void, IOException> readBlockwise(@NotNull Reader in,
                                                          @NotNull BiParamTask<char[], Integer, ? extends IOException> consumer) {
        return readBlockwise(1024, in, consumer);
    }

    /**
     * Convenience method to pass updating methods as reference into a blockwise read operation.
     * <p>
     * Reads all available characters from a reader in blocks of a certain size and calls a consumer with the respective buffer.
     *
     * @param blockSize the block size
     * @param in        the stream to read from
     * @param consumer  the consumer, called with the buffer, an offset (which is always {@code 0} and the read length
     */
    @NotNull
    public static Expect<Void, IOException> updateBlockwise(int blockSize, @NotNull Reader in,
                                                            @NotNull TriParamTask<char[], Integer, Integer, ? extends IOException> consumer) {
        return readBlockwise(blockSize, in, (buf, len) -> consumer.execute(buf, 0, len));
    }

    /**
     * Reads all available characters from a reader in blocks of a certain size and calls a consumer with the respective buffer.
     *
     * @param blockSize the block size
     * @param in        the stream to read from
     * @param consumer  the consumer, called with the buffer and the read length
     */
    @NotNull
    public static Expect<Void, IOException> readBlockwise(int blockSize, @NotNull Reader in,
                                                          @NotNull BiParamTask<char[], Integer, ? extends IOException> consumer) {
        if (blockSize <= 0)
            throw new IllegalArgumentException("Invalid blocksize");

        return Expect.compute(() -> {
            char[] buf = new char[blockSize];
            int len;
            while ((len = in.read(buf)) != -1) {
                consumer.execute(buf, len);
            }
        });
    }


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
    @NotNull
    public static Expect<Void, IOException> write(@NotNull InputStream in, @NotNull OutputStream out) {
        return readBlockwise(1024, in, (buf, len) -> out.write(buf, 0, len));
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
    @NotNull
    public static Expect<Void, IOException> write(@NotNull Reader in, @NotNull Writer out) {
        return readBlockwise(1024, in, (buf, len) -> out.write(buf, 0, len));
    }

    /**
     * Reads all bytes from an input stream. This method does not close the stream.
     *
     * @param in the stream to read from
     * @return the bytes read
     * @see #write(InputStream, OutputStream)
     */
    @NotNull
    public static Expect<byte[], IOException> read(@NotNull InputStream in) {
        return Expect.compute(() -> {
            try (var bout = new ByteArrayOutputStream()) {
                write(in, bout).rethrow(IOException.class);
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
    @NotNull
    public static Expect<char[], IOException> read(@NotNull Reader reader) {
        return Expect.compute(() -> {
            try (var cout = new CharArrayWriter()) {
                write(reader, cout).rethrow(IOException.class);
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
    @NotNull
    public static Expect<String, IOException> toString(@NotNull InputStream in) {
        return read(in).map(String::new);
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
    @NotNull
    public static Thread redirect(@NotNull InputStream in, @NotNull OutputStream out) {
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
    @NotNull
    public static Thread redirect(@NotNull InputStream in, @NotNull OutputStream out, @NotNull ThreadFactory factory) {
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
    @NotNull
    public static Task<IOException> prepareRedirect(@NotNull InputStream in, @NotNull OutputStream out) {
        return () -> {
            int b;
            while (!Thread.currentThread().isInterrupted() && (b = in.read()) != -1) {
                out.write(b);
            }
        };
    }
}
