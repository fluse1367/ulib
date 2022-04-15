package eu.software4you.ulib.loader.impl;

import lombok.SneakyThrows;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
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

    public static List<Class<?>> tryClasses(ClassSup... sups) {
        List<Class<?>> li = new ArrayList<>(sups.length);

        for (var sup : sups) {
            try {
                li.add(sup.get());
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                // ignored
            }
        }

        return li;
    }

    public interface ClassSup {
        Class<?> get() throws ClassNotFoundException, NoClassDefFoundError;
    }
}
