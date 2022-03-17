package eu.software4you.ulib.core.io;

import eu.software4you.ulib.core.util.Expect;

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
    public Expect<Void, IOException> writeTo(OutputStream out) {
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
    public String toString() {
        return bout.toString();
    }

    /**
     * @see ByteArrayOutputStream#toString(String)
     */
    public Expect<String, UnsupportedEncodingException> toString(String charsetName) {
        return Expect.compute(() -> bout.toString(charsetName));
    }

    /**
     * @see ByteArrayOutputStream#toString(Charset)
     */
    public String toString(Charset charset) {
        return bout.toString(charset);
    }
}
