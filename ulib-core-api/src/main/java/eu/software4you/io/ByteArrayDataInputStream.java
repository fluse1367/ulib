package eu.software4you.io;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public class ByteArrayDataInputStream extends DataInputStream {


    /**
     * @see ByteArrayInputStream#ByteArrayInputStream(byte[]) ByteArrayInputStream(byte[])
     */
    public ByteArrayDataInputStream(byte[] buf) {
        super(new ByteArrayInputStream(buf));
    }
}
