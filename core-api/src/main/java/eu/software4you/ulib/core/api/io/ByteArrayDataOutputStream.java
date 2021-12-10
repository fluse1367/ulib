package eu.software4you.ulib.core.api.io;

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
    public void writeTo(OutputStream out) throws IOException {
        bout.writeTo(out);
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
    public String toString(String charsetName) throws UnsupportedEncodingException {
        return bout.toString(charsetName);
    }

    /**
     * @see ByteArrayOutputStream#toString(Charset)
     */
    public String toString(Charset charset) {
        return bout.toString(charset);
    }
}
