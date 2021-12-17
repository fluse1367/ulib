package eu.software4you.ulib.loader.install.provider;

import java.io.File;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

public final class DependencyTransformer {

    private final HashMap<String, Consumer<File>> transformers = new HashMap<>();

    public DependencyTransformer() {

    }

    public void transform(String coords, File targetFile) {
        Optional.ofNullable(transformers.get(coords)).ifPresent(c -> c.accept(targetFile));
    }
}
