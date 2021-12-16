package eu.software4you.ulib.loader.install;

import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

@SuppressWarnings("DuplicatedCode")
final class Util {
    @SneakyThrows
    static void write(InputStream in, OutputStream out) {
        try (var is = in; var os = out) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
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
