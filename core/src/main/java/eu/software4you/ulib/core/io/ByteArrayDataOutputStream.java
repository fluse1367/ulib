package eu.software4you.ulib.core.io;

import eu.software4you.ulib.core.util.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.Charset;

public class ByteArrayDataOutputStream extends DataOutputStream {
    private final ByteArrayOutputStream bout;

    /**
     * @see ByteArrayOutputStream#ByteArrayOutputStream() ByteArrayOutputStream()
     */
    public ByteArrayDataOutputStream() {
        super(new ByteArrayOutputStream());
        this.bout = (ByteArrayOutputStream) super.out;
    }

    /**
     * @see ByteArrayOutputStream#writeTo(OutputStream)
     */
    @NotNull
    public Expect<Void, IOException> writeTo(@NotNull OutputStream out) {
        return Expect.compute(() -> bout.writeTo(out));
    }

    /**
     * @see ByteArrayOutputStream#reset()
     */
    public void reset() {
        bout.reset();
    }

    /**
     * @see ByteArrayOutputStream#toByteArray()
     */
    public byte[] toByteArray() {
        return bout.toByteArray();
    }

    /**
     * @see ByteArrayOutputStream#toString()
     */
    @NotNull
    public String toString() {
        return bout.toString();
    }

    /**
     * @see ByteArrayOutputStream#toString(String)
     */
    @NotNull
    public Expect<String, UnsupportedEncodingException> toString(@NotNull String charsetName) {
        return Expect.compute(() -> bout.toString(charsetName));
    }

    /**
     * @see ByteArrayOutputStream#toString(Charset)
     */
    @NotNull
    public String toString(@NotNull Charset charset) {
        return bout.toString(charset);
    }
}
