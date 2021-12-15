package eu.software4you.ulib.loader;

import eu.software4you.ulib.core.api.utils.ChecksumUtils;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.jar.JarFile;

public class Extractor {

    private final File modsDir;
    private final File libsDir;
    private final JarFile jar;

    @SneakyThrows
    public Extractor() {
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
    private String[] readManifest(String what) {
        return jar.getManifest().getMainAttributes().getValue(what + "-Files").split(", ");
    }

    public File[] extract() {
        var f1 = extract("Module", "modules");
        var f2 = extract("Library", "libs");
        var f = new File[f1.length + f2.length];
        System.arraycopy(f1, 0, f, 0, f1.length);
        System.arraycopy(f2, 0, f, f1.length, f2.length);
        return f;
    }

    @SneakyThrows
    public File[] extract(String what, String insideDir) {
        String[] toExtract = readManifest(what);
        File[] files = new File[toExtract.length];

        for (int i = 0; i < toExtract.length; i++) {
            files[i] = extract(toExtract[i], libsDir, insideDir);
        }

        return files;
    }

    @SneakyThrows
    private File extract(String name, File dir, String insideDir) {
        File file = new File(dir, name);
        String location = "META-INF/" + insideDir + "/" + name;

        try (var in = jar.getInputStream(jar.getEntry(location))) {

            if (file.exists()) { // library already exists, check hash
                if (ChecksumUtils.getCRC32(in) == ChecksumUtils.getCRC32(new FileInputStream(file)))
                    return file; // same hash, skip extraction
                in.reset();
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

        }
        return file;
    }

}
