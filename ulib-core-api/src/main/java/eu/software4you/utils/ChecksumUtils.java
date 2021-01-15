package eu.software4you.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ChecksumUtils {

    /**
     * Generates the checksum of a {@link File}
     */
    public static String getFileChecksum(MessageDigest digest, File file) throws IOException {
        //Get file input stream for reading the file content
        return getFileChecksum(digest, new FileInputStream(file));
    }

    /**
     * Generates the checksum of an {@link InputStream}
     */
    public static String getFileChecksum(MessageDigest digest, InputStream in) throws IOException {

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
        for (int i = 0; i < bytes.length; i++) {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        //return complete hash
        return sb.toString();
    }

    /**
     * Generates the SHA-256 checksum of the own {@link File} from its {@link Class}
     */
    public static String getChecksum(Class<?> clazz) {
        return getChecksum(clazz, "SHA-256");
    }

    /**
     * Generates the checksum of the own {@link File} from its {@link Class}
     */
    public static String getChecksum(Class<?> clazz, String digest) {
        String checksum = null;
        try {
            MessageDigest dig = MessageDigest.getInstance(digest);
            checksum = getFileChecksum(dig, new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI()));
        } catch (NoSuchAlgorithmException | URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return checksum;
    }

}
