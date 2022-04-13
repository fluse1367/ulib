package eu.software4you.ulib.loader.impl;

import lombok.SneakyThrows;

import java.io.*;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@SuppressWarnings("DuplicatedCode")
public final class Util {
    public static String classify(String name) {
        return name.replace('.', '/').concat(".class");
    }

    @SneakyThrows
    public static void write(InputStream in, OutputStream out) {
        try (var is = in; var os = out) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
    }

    public static long getCRC32(InputStream in) throws IOException {
        Checksum sum = new CRC32();

        byte[] buff = new byte[1024];
        int len;
        while ((len = in.read(buff)) != -1) {
            sum.update(buff, 0, len);
        }
        in.close();

        return sum.getValue();
    }
}
