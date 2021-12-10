package eu.software4you.ulib.core.api.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ChecksumUtils {

    /**
     * Generates the checksum of a {@link File}
     */
    @NotNull
    public static String getFileChecksum(@NotNull MessageDigest digest, @NotNull File file) throws IOException {
        //Get file input stream for reading the file content
        return getFileChecksum(digest, new FileInputStream(file));
    }

    /**
     * Generates the checksum of an {@link InputStream}
     */
    @NotNull
    public static String getFileChecksum(@NotNull MessageDigest digest, @NotNull InputStream in) throws IOException {

        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0;

        //Read file data and update in message digest
        while ((bytesCount = in.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        }

        //close the stream; We don't need it now.
        in.close();

        //Get the hash's bytes
        byte[] bytes = digest.digest();

        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    /**
     * Generates the SHA-256 checksum of the own {@link File} from its {@link Class}
     */
    @Nullable
    public static String getChecksum(@NotNull Class<?> clazz) {
        return getChecksum(clazz, "SHA-256");
    }

    /**
     * Generates the checksum of the own {@link File} from its {@link Class}
     */
    @Nullable
    public static String getChecksum(@NotNull Class<?> clazz, @NotNull String digest) {
        String checksum = null;
        try {
            MessageDigest dig = MessageDigest.getInstance(digest);
            checksum = getFileChecksum(dig, new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (NoSuchAlgorithmException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }


    /**
     * Computes a CRC32 checksum from an input stream. This method closes the stream.
     *
     * @param in the stream to compute the checksum from
     * @return the checksum
     * @throws IOException inherited from {@link InputStream#read()} and {@link InputStream#close()}
     * @see InputStream#read()
     * @see InputStream#close()
     */
    public static long getCRC32(@NotNull InputStream in) throws IOException {
        Checksum sum = new CRC32();

        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) != -1) {
            sum.update(buff, 0, len);
        }
        in.close();

        return sum.getValue();
    }

    /**
     * Computes a CRC32 checksum from a byte array.
     *
     * @param arr the array to compute the checksum from
     * @return the checksum
     */
    public static long getCRC32(byte[] arr) {
        Checksum sum = new CRC32();
        sum.update(arr, 0, arr.length);
        return sum.getValue();
    }

}
