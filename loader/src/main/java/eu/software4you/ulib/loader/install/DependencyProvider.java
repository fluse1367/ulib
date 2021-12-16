package eu.software4you.ulib.loader.install;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static eu.software4you.ulib.loader.install.Util.getCRC32;
import static eu.software4you.ulib.loader.install.Util.write;

final class DependencyProvider {

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z-0-9._]+\\.jar\\b", Pattern.MULTILINE);
    final File modsDir;
    final File libsDir;
    private final JarFile jar;

    @SneakyThrows
    DependencyProvider() {
        var dataDir = new File(System.getProperty("ulib.directory.main", ".ulib"));
        this.modsDir = new File(dataDir, "modules");
        this.libsDir = new File(modsDir, "libraries");
        this.jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));

        initDir(dataDir);
        initDir(modsDir);
        initDir(libsDir);
    }

    private void initDir(File f) {
        if (!f.exists())
            if (!f.mkdirs()) {
                throw new RuntimeException("Cannot create '" + f + "' directory!");
            }
    }

    @SneakyThrows
    String readManifestRaw(String what) {
        return jar.getManifest().getMainAttributes().getValue(what);
    }

    private Collection<String> readManifest(String what) {
        var val = readManifestRaw(what);
        if (val == null)
            return Collections.emptyList();
        var matcher = PATTERN.matcher(val);
        List<String> matches = new LinkedList<>();
        while (matcher.find()) {
            var match = matcher.group();
            matches.add(match);
        }
        return matches;
    }

    Collection<File> extractLibrary() {
        return extract("Library-Files", modsDir);
    }

    Collection<File> extractModule() {
        return extract("Module-Files", modsDir);
    }

    Collection<File> extractSuper() {
        return extract("Super-Modules", modsDir);
    }

    Collection<File> downloadAdditional() {
        List<File> li = new LinkedList<>();

        var downloader = new DependencyDownloader();
        var matcher = DependencyDownloader.PATTERN.matcher(readManifestRaw("Libraries"));

        while (matcher.find()) {
            li.add(downloader.download(matcher.group(), libsDir));
        }

        return li;
    }

    @SneakyThrows
    public Collection<File> extract(String what, File dir) {
        return readManifest(what).stream()
                .map(elem -> extractSingle(elem, dir))
                .toList();
    }

    @SneakyThrows
    private File extractSingle(String name, File dir) {
        File file = new File(dir, name);
        String location = "META-INF/jars/" + name;
        var en = jar.getEntry(location);

        var in = jar.getInputStream(en);

        if (file.exists()) { // library already exists, check hash
            if (getCRC32(in) == getCRC32(new FileInputStream(file)))
                return file; // same hash, skip extraction
            in.close();
            in = jar.getInputStream(en);
        }
        // extract!
        write(in, new FileOutputStream(file, false));
        return file;
    }
}
