package eu.software4you.ulib.loader.install.provider;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@SuppressWarnings("DuplicatedCode")
final class Util {
    static String classify(String name) {
        return name.replace('.', '/').concat(".class");
    }

    @SneakyThrows
    static void write(InputStream in, OutputStream out) {
        try (var is = in; var os = out) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1) {
                os.write(buffer, 0, read);
            }
            os.flush();
        }
    }

    static long getCRC32(InputStream in) throws IOException {
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
