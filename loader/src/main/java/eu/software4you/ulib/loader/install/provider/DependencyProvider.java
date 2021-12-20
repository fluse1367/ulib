package eu.software4you.ulib.loader.install.provider;

import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

import static eu.software4you.ulib.loader.install.provider.Util.getCRC32;
import static eu.software4you.ulib.loader.install.provider.Util.write;

public final class DependencyProvider {

    private static final Pattern LAYER_1 = Pattern.compile("(\\w+)\\b=\\[([^=]*)]", Pattern.MULTILINE);
    private static final Pattern LAYER_2 = Pattern.compile("[a-zA-Z-0-9._]+\\.jar\\b", Pattern.MULTILINE);

    final File modsDir;
    final File libsDir;
    private final JarFile jar;
    private final Map<String, Collection<String>> modulesMap;

    @SneakyThrows
    public DependencyProvider() {
        var dataDir = new File(System.getProperty("ulib.directory.main", ".ulib"));
        this.modsDir = new File(dataDir, "modules");
        this.libsDir = new File(modsDir, "libraries");
        this.jar = new JarFile(new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI()));

        initDir(dataDir);
        initDir(modsDir);
        initDir(libsDir);

        this.modulesMap = readModules();
    }

    private void initDir(File f) {
        if (!f.exists())
            if (!f.mkdirs()) {
                throw new RuntimeException("Cannot create '" + f + "' directory!");
            }
    }

    private Map<String, Collection<String>> readModules() {
        var val = readManifest("Modules");
        if (val == null)
            throw new IllegalArgumentException("Modules key not found");

        Map<String, Collection<String>> map = new HashMap<>();
        var l1Matcher = LAYER_1.matcher(val);
        while (l1Matcher.find()) {
            String group = l1Matcher.group(1);

            var l2Matcher = LAYER_2.matcher(l1Matcher.group(2));
            List<String> modules = new LinkedList<>();
            while (l2Matcher.find()) {
                modules.add(l2Matcher.group());
            }

            map.putIfAbsent(group, modules);
        }

        return map;
    }

    @SneakyThrows
    private String readManifest(String what) {
        return jar.getManifest().getMainAttributes().getValue(what);
    }

    public Collection<File> extractModules(Collection<String> what) {
        return what.stream()
                .map(this::extractModule)
                .flatMap(Collection::stream)
                .toList();
    }

    public Collection<File> extractModule(String what) {
        return extract(what, modsDir);
    }

    public Collection<File> downloadAdditional(Predicate<String> coordsFilter, BiConsumer<String, File> callback) {
        List<File> li = new LinkedList<>();

        var downloader = new DependencyDownloader();
        var matcher = DependencyDownloader.PATTERN.matcher(readManifest("Libraries"));

        while (matcher.find()) {
            var coords = matcher.group();
            if (!coordsFilter.test(coords))
                continue;
            li.add(downloader.download(coords, libsDir,
                    f -> callback.accept(coords, f)));
        }

        return li;
    }

    @SneakyThrows
    Collection<File> extract(String what, File dir) {
        if (!modulesMap.containsKey(what))
            return List.of();

        return modulesMap.get(what).stream()
                .map(elem -> extractJar(elem, dir))
                .toList();
    }

    @SneakyThrows
    private File extractJar(String name, File dir) {
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
