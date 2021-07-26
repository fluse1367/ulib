package eu.software4you.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HexUtil {
    public static String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();
        for (byte b : array) {
            sb.append(Integer.toHexString((b
                                           & 0xFF) | 0x100), 1, 3);
        }
        return sb.toString();
    }

    /**
     * @param message input string
     * @param digest  digest to be used
     * @return hexed input string
     * @throws NoSuchAlgorithmException if digest was not found
     */
    public static String hex(String message, String digest) throws NoSuchAlgorithmException {
        MessageDigest md =
                MessageDigest.getInstance(digest);
        byte[] b = new byte[0];
        try {
            b = message.getBytes("CP1252");
        } catch (Exception e) {
        }
        return HexUtil.hex(md.digest(b));
    }

    /**
     * @param message input string
     * @param digest  digest to be used
     * @return hexed input string
     * @throws NoSuchAlgorithmException if digest was not found
     */
    public static String hex(String message, Digest digest) throws NoSuchAlgorithmException {
        return hex(message, digest.digest);
    }

    /**
     * @param message input string
     * @return md5 hexed input string
     */
    public static String md5Hex(String message) {
        try {
            return hex(message, "MD5");
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }

    public enum Digest {
        MD2("MD2"),
        MD5("MD5"),
        SHA("SHA-1"),
        SHA224("SHA-224"),
        SHA256("SHA-256"),
        SHA384("SHA-384"),
        SHA512("SHA-512"),
        ;
        private final String digest;

        Digest(String digest) {
            this.digest = digest;
        }
    }
}
