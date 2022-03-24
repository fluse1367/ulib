package eu.software4you.ulib.core.impl;

import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.core.impl.configuration.yaml.YamlSerializer;
import eu.software4you.ulib.core.impl.inject.*;
import eu.software4you.ulib.core.io.IOUtil;
import lombok.Getter;
import lombok.SneakyThrows;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.util.Objects;

public final class Internal {

    @Getter
    private static final File dataDir, cacheDir, localMavenDir;
    @Getter
    private static final boolean unsafeOperations, forceSync;
    @Getter
    private static Instrumentation instrumentation;

    private static YamlConfiguration yaml;
    private static Boolean clOverride;

    static {
        mkdirs(dataDir = new File(System.getProperty("ulib.directory.main", ".ulib")));

        yaml = loadConfig();
        clOverride = yaml.bool("override-command-line").orElse(false);


        mkdirs(cacheDir = new File(get("directories.cache", "ulib.directory.cache",
                String.format("%s%scache", dataDir.getPath(), File.separator))));
        mkdirs(localMavenDir = new File(get("directories.libraries", "ulib.directory.libraries",
                String.format("%s%slibraries", dataDir.getPath(), File.separator))));

        forceSync = get("force-synchronous-work", "ulib.forcesync", "false").equalsIgnoreCase("true");
        unsafeOperations = get("unsafe-operations", "ulib.unsafe_operations", "deny").equalsIgnoreCase("allow");

        // let gc do its work
        yaml = null;
        clOverride = null;
    }

    private static String get(String yamlPath, String propsKey, String def) {
        String pVal = System.getProperty(propsKey), cVal = yaml.string(yamlPath).orElse(pVal);
        String preferred = clOverride ? cVal : pVal, second = clOverride ? preferred : cVal;

        if (preferred != null && !preferred.isEmpty())
            return preferred;
        if (second != null && !second.isEmpty())
            return second;
        return def;
    }

    @SneakyThrows
    private static void mkdirs(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs())
                throw new Exception(String.format("Directory %s cannot be created ", dir));
        }
    }

    @SneakyThrows
    private static YamlConfiguration loadConfig() {
        YamlSerializer serializer = YamlSerializer.getInstance();

        var conf = new File(dataDir, "config.yml");
        if (!conf.exists()) {
            try (var in = getCurrentConfig();
                 var out = new FileOutputStream(conf);
                 var reader = new FileReader(conf)) {
                IOUtil.write(in, out).rethrow();
                return serializer.deserialize(reader);
            }
        }
        YamlConfiguration current, saved;
        try (var in = new InputStreamReader(getCurrentConfig()); var reader = new FileReader(conf)) {
            current = serializer.deserialize(in);
            saved = serializer.deserialize(reader);
        }

        // check if upgrade is required
        int cv = current.int32("config-version").orElse(-1), sv = saved.int32("config-version").orElse(-2);
        if (cv <= sv)
            return saved;

        // upgrade config.yml with newer contents
        saved.set("config-version", cv);
        current.getValues(true).forEach((k, v) -> {
            if (!saved.isSet(k)) {
                saved.set(k, v);
                saved.setComments(k, current.getComments(k));
            }
        });

        try (var out = new FileWriter(conf)) {
            saved.dump(out);
        }
        return saved;
    }

    private static InputStream getCurrentConfig() {
        return Objects.requireNonNull(Internal.class.getResourceAsStream("/META-INF/coreconfig.yml"), "Configuration not found");
    }

    @SneakyThrows
    private static void widenModuleAccess() {
        var method = Module.class.getDeclaredMethod("implAddReadsAllUnnamed");
        method.setAccessible(true);

        var modules = AccessibleObjectTransformer.class.getModule().getLayer().modules().stream()
                .filter(m -> m.getName().startsWith("ulib."))
                .toArray(Module[]::new);

        for (Module module : modules) {
            method.invoke(module);
        }
    }

    public static void agentInit(Instrumentation instrumentation) {
        if (Internal.instrumentation != null)
            throw new IllegalStateException();
        Internal.instrumentation = Objects.requireNonNull(instrumentation);

        AccessibleObjectTransformer.acquirePrivileges();
        widenModuleAccess();
        PropertiesLock.lockSystemProperties(AccessibleObjectTransformer.SUDO_KEY, InjectionManager.HOOKING_KEY);
    }
}
