package eu.software4you.ulib.core.api.util;

import eu.software4you.ulib.core.api.util.value.Expect;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class HashUtil {
    /**
     * Computes the hash from a given stream. This method does not close the stream.
     *
     * @param stream the stream to compute the hash from
     * @param digest the digest to use
     * @return the hex representation of the stream's hash
     */
    @NotNull
    public static Expect<String, IOException> computeHex(@NotNull InputStream stream, @NotNull MessageDigest digest) {
        return Expect.compute(() -> Conversions.toHex(computeHash(stream, digest).orElseRethrow()));
    }

    /**
     * Computes the hash from a given stream. This method does not close the stream.
     *
     * @param in     the stream to compute the hash from
     * @param digest the digest to use
     * @return the computed hash
     * @see Conversions#toHex(byte[])
     */
    public static Expect<byte[], IOException> computeHash(@NotNull InputStream in, @NotNull MessageDigest digest) {
        return Expect.compute(() -> {
            var buff = new byte[1024];
            int len;
            while ((len = in.read()) != -1) {
                digest.update(buff, 0, len);
            }
            return digest.digest();
        });
    }

    /**
     * Computes the hash from a given byte array.
     *
     * @param bytes  the bytes to compute the hash from
     * @param digest the digest to use
     * @return the computed hash
     * @see Conversions#toHex(byte[])
     */
    public static byte[] computeHash(byte[] bytes, @NotNull MessageDigest digest) {
        digest.update(bytes);
        return digest.digest();
    }

    /**
     * Computes a CRC32 checksum from an input stream. This method does not close the stream.
     *
     * @param in the stream to compute the checksum from
     * @return the checksum
     * @see InputStream#read()
     * @see InputStream#close()
     */
    public static Expect<Long, IOException> computeCRC32(@NotNull InputStream in) {
        return Expect.compute(() -> {
            Checksum sum = new CRC32();

            byte[] buff = new byte[1024];
            int len;
            while ((len = in.read(buff)) != -1) {
                sum.update(buff, 0, len);
            }

            return sum.getValue();
        });
    }

    /**
     * Computes a CRC32 checksum from a byte array.
     *
     * @param arr the array to compute the checksum from
     * @return the checksum
     */
    public static long computeCRC32(byte[] arr) {
        Checksum sum = new CRC32();
        sum.update(arr);
        return sum.getValue();
    }
}
