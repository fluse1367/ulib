package eu.software4you.ulib.loader;

import lombok.SneakyThrows;

import java.io.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

final class Extractor {

    private static final Pattern PATTERN = Pattern.compile("[a-zA-Z-0-9._]+\\.jar\\b", Pattern.MULTILINE);
    private final File modsDir;
    private final File libsDir;
    private final JarFile jar;

    @SneakyThrows
    Extractor() {
        var dataDir = new File(System.getProperty("ulib.directory.main", ".ulib"));
        this.modsDir = new File(dataDir, "modules");
        this.libsDir = new File(modsDir, "libs");
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
    private Collection<String> readManifest(String what) {
        var val = jar.getManifest().getMainAttributes().getValue(what + "-Files");
        var matcher = PATTERN.matcher(val);
        List<String> matches = new LinkedList<>();
        while (matcher.find()) {
            var match = matcher.group();
            matches.add(match);
        }
        return matches;
    }

    File[] extract() {
        var f1 = extract("Module", modsDir);
        var f2 = extract("Library", libsDir);
        var f = new File[f1.length + f2.length];
        System.arraycopy(f1, 0, f, 0, f1.length);
        System.arraycopy(f2, 0, f, f1.length, f2.length);
        return f;
    }

    @SneakyThrows
    public File[] extract(String what, File dir) {
        var toExtract = readManifest(what);
        File[] files = new File[toExtract.size()];

        int i = 0;
        for (var elem : toExtract) {
            files[i++] = extractSingle(elem, dir);
        }

        return files;
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
        try (var out = new FileOutputStream(file, false)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        }

        return file;
    }

    @SuppressWarnings("DuplicatedCode")
    private static long getCRC32(InputStream in) throws IOException {
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
