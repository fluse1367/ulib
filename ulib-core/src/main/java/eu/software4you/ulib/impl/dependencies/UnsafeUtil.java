package eu.software4you.ulib.impl.dependencies;

import eu.software4you.dependencies.DependencyLoader;
import eu.software4you.http.ChecksumFile;
import eu.software4you.io.IOUtil;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.file.Paths;

final class UnsafeUtil {
    @SneakyThrows
    static void dependUnsafe(String coords) {
        String base = "https://repo1.maven.org/maven2/";
        String[] parts = coords.split(":");

        String group = parts[0];
        String name = parts[1];
        String version = parts[2];

        String request = String.format("%s/%s/%s/%s-%s.jar", group.replace(".", "/"),
                name, version, name, version);

        URL url = new URL(base + request);

        File root = ULib.get().getLibrariesUnsafeDir();
        ChecksumFile cf = new ChecksumFile("SHA-1", null, root.getParentFile(),
                root.getName(), Paths.get(request), dest -> generate(dest, url));
        cf.ensure();
        DependencyLoader.load(cf.getFile(), UnsafeUtil.class.getClassLoader(), false);
    }

    @SneakyThrows
    private static void generate(File file, URL url) {
        try (var in = url.openStream();
             var out = new FileOutputStream(file)) {
            IOUtil.write(in, out);
        }
    }
}
