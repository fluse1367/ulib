package eu.software4you.ulib.loader.install.provider;

import lombok.SneakyThrows;

import java.io.*;
import java.util.Base64;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;

import static eu.software4you.ulib.loader.install.provider.Util.getCRC32;
import static eu.software4you.ulib.loader.install.provider.Util.write;

public final class DependencyProvider {

    final File modsDir;
    final File libsDir;
    private final JarFile jar;
    private final Map<String, Map<String, ?>> modulesGraph;

    @SneakyThrows
    public DependencyProvider() {
        var dataDir = new File(System.getProperty("ulib.directory.main", ".ulib"));
        this.modsDir = new File(dataDir, "modules");
        this.libsDir = new File(modsDir, "libraries");
        this.jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));

        initDir(dataDir);
        initDir(modsDir);
        initDir(libsDir);


        // read modules graph
        try {
            var val = jar.getManifest().getMainAttributes().getValue("Modules");
            if (val == null)
                throw new IllegalArgumentException("Modules key not found");

            // deserialize modules graph
            byte[] serialized = Base64.getDecoder().decode(val);
            try (var in = new ObjectInputStream(new ByteArrayInputStream(serialized))) {
                this.modulesGraph = (Map<String, Map<String, ?>>) in.readObject();
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("MANIFEST.MF file invalid", e);
        }
    }

    private void initDir(File f) {
        if (!f.exists())
            if (!f.mkdirs()) {
                throw new RuntimeException("Cannot create '" + f + "' directory!");
            }
    }

    public Collection<File> extractModules(Collection<String> what) {
        return what.stream()
                .map(this::extractModule)
                .flatMap(Collection::stream)
                .toList();
    }

    public Collection<File> extractModule(String what) {
        return extractJars(what, modsDir);
    }

    @SneakyThrows
    Collection<File> extractJars(String module, File dir) {
        return ((Collection<String>) modulesGraph.get(module).get("jars")).stream()
                .map(elem -> extractJar(elem, dir))
                .toList();
    }

    @SneakyThrows
    private File extractJar(String jarName, File destDir) {
        File dest = new File(destDir, jarName);
        String location = "META-INF/jars/" + jarName;
        var en = jar.getEntry(location);

        var in = jar.getInputStream(en);

        if (dest.exists()) { // library already exists, check hash
            if (getCRC32(in) == getCRC32(new FileInputStream(dest)))
                return dest; // same hash, skip extraction
            in.close();
            in = jar.getInputStream(en);
        }
        // extract!
        write(in, new FileOutputStream(dest, false));
        return dest;
    }

    public Collection<File> downloadLibraries(Collection<String> modules, Predicate<String> coordsFilter, BiConsumer<String, File> callback) {
        return modules.stream()
                .map(module -> downloadLibraries(module, coordsFilter, callback))
                .flatMap(Collection::stream)
                .toList();
    }

    public Collection<File> downloadLibraries(String module, Predicate<String> coordsFilter, BiConsumer<String, File> callback) {
        var downloader = new DependencyDownloader();
        return ((Collection<String>) modulesGraph.get(module).get("libraries")).stream()
                .filter(coordsFilter)
                .map(coords -> downloader.download(coords, libsDir, f -> callback.accept(coords, f)))
                .toList();
    }
}
