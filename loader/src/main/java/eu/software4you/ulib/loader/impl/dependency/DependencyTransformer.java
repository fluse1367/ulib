package eu.software4you.ulib.loader.impl.dependency;

import lombok.SneakyThrows;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;
import java.util.jar.*;

import static eu.software4you.ulib.loader.impl.Util.classify;

public final class DependencyTransformer {

    private final HashMap<Predicate<Set<String>>, Runnable> transformers = new HashMap<>();
    private final HashMap<String, File> cached = new HashMap<>();

    public DependencyTransformer() {
        // org.apache.maven.model.merge.MavenModelMerger in maven-model-builder -> maven-model
        add2Transformer("org.apache.maven:maven-model-builder:3.8.4", "org.apache.maven:maven-model:3.8.4",
                s -> s.equals(classify("org.apache.maven.model.merge.MavenModelMerger")),
                s -> false);

        // org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException in maven-artifact -> maven-repository-metadata
        add2Transformer("org.apache.maven:maven-artifact:3.8.4", "org.apache.maven:maven-repository-metadata:3.8.4",
                s -> s.equals(classify("org.apache.maven.artifact.repository.metadata.RepositoryMetadataStoreException")),
                s -> false);

        // add 'Automatic-Module-Name' entry to manifest file for xseries
        addManifestTransformer("{{maven.xseries}}", (Consumer<Manifest>) man ->
                man.getMainAttributes().putValue("Automatic-Module-Name", "xseries"));
    }

    public void transform(String coords, File targetFile) {
        cached.putIfAbsent(coords, targetFile);
        var set = cached.keySet();
        transformers.forEach((p, t) -> {
            if (p.test(set))
                t.run();
        });
    }

    public void addManifestTransformer(String coords, Consumer<Manifest> visitor) {
        addManifestTransformer(coords, man -> {
            visitor.accept(man);
            return man;
        });
    }

    public void addManifestTransformer(String coords, Function<Manifest, Manifest> transformer) {
        addTransformer(set -> set.contains(coords), () -> {
            try {
                var f = cached.get(coords);
                var tmp = Files.move(f.toPath(), Files.createTempFile(f.getName(), null),
                        StandardCopyOption.REPLACE_EXISTING).toFile();
                tmp.deleteOnExit();

                var jf = new JarFile(tmp);
                var man = transformer.apply(jf.getManifest());
                jf.close();

                try (var jin = new JarInputStream(new FileInputStream(tmp));
                     var jout = new JarOutputStream(new FileOutputStream(f), man)) {
                    iterate(jin, jout, null, je -> false);
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void add2Transformer(String coords1, String coords2, Predicate<String> _1to2, Predicate<String> _2to1) {
        addTransformer(set -> set.containsAll(Arrays.asList(coords1, coords2)),
                () -> switchEntries(cached.get(coords1), cached.get(coords2), _1to2, _2to1)
        );
    }

    public void addTransformer(Predicate<Set<String>> test, Runnable action) {
        transformers.putIfAbsent(test, action);
    }

    @SneakyThrows
    private void switchEntries(File file1, File file2, Predicate<String> _1to2, Predicate<String> _2to1) {
        var tmp1 = Files.move(file1.toPath(), Files.createTempFile(file1.getName(), null),
                StandardCopyOption.REPLACE_EXISTING);

        var tmp2 = Files.move(file2.toPath(), Files.createTempFile(file2.getName(), null),
                StandardCopyOption.REPLACE_EXISTING);

        copyAndSwitch(tmp1, file1.toPath(), tmp2, file2.toPath(),
                e -> _1to2.test(e.getName()), e -> _2to1.test(e.getName()));
    }

    @SneakyThrows
    private void copyAndSwitch(Path source1, Path dest1, Path source2, Path dest2, Predicate<JarEntry> _1to2, Predicate<JarEntry> _2to1) {
        try (
                // file 1
                JarInputStream in1 = new JarInputStream(new FileInputStream(source1.toFile()));
                JarOutputStream out1 = new JarOutputStream(new FileOutputStream(dest1.toFile()), in1.getManifest());

                // file 2
                JarInputStream in2 = new JarInputStream(new FileInputStream(source2.toFile()));
                JarOutputStream out2 = new JarOutputStream(new FileOutputStream(dest2.toFile()), in2.getManifest())
        ) {
            // iterate over entries in jar file
            iterate(in1, out1, out2, _1to2);
            iterate(in2, out2, out1, _2to1);
        }
    }

    @SneakyThrows
    private void iterate(JarInputStream in, JarOutputStream primaryOut, JarOutputStream secondaryOut, Predicate<JarEntry> writeToSecondary) {
        JarEntry entry;
        while ((entry = in.getNextJarEntry()) != null) {

            JarOutputStream out = writeToSecondary.test(entry) ? secondaryOut : primaryOut;

            // copy entry to new jar file
            out.putNextEntry(new JarEntry(entry.getName()));
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) != -1) {
                out.write(buf, 0, len);
            }
            out.closeEntry();
        }
    }
}
