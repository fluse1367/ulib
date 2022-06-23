package eu.software4you.ulib.core.impl;

import eu.software4you.ulib.core.configuration.Configuration;
import eu.software4you.ulib.core.configuration.YamlConfiguration;
import eu.software4you.ulib.core.function.Func;
import eu.software4you.ulib.core.impl.configuration.yaml.YamlSerializer;
import eu.software4you.ulib.core.impl.inject.*;
import eu.software4you.ulib.core.io.IOUtil;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.*;
import lombok.*;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Internal {

    @Getter
    private static final Path dataDir, cacheDir, localMavenDir;
    @Getter
    private static final boolean unsafeOperations, forceSync;
    private static final Set<Thread> sudoThreads = ConcurrentHashMap.newKeySet();
    @Getter
    private static Instrumentation instrumentation;

    private static YamlConfiguration yaml;
    private static Boolean clOverride;

    static {
        mkdirs(dataDir = Path.of(System.getProperty("ulib.directory.main", ".ulib")));

        yaml = loadConfig();
        clOverride = yaml.bool("override-command-line").orElse(false);


        mkdirs(cacheDir = Path.of(get("directories.cache", "ulib.directory.cache",
                String.format("%s%scache", dataDir, File.separator))));
        mkdirs(localMavenDir = Path.of(get("directories.libraries", "ulib.directory.libraries",
                String.format("%s%slibraries", dataDir, File.separator))));

        forceSync = get("force-synchronous-work", "ulib.forcesync", "false").equalsIgnoreCase("true");
        unsafeOperations = get("unsafe-operations", "ulib.unsafe_operations", "deny").equalsIgnoreCase("allow");

        // let gc do its work
        yaml = null;
        clOverride = null;
    }

    // - class init helpers  -

    private static String get(String yamlPath, String propsKey, String def) {
        String pVal = System.getProperty(propsKey), cVal = yaml.string(yamlPath).orElse(pVal);
        String preferred = clOverride ? cVal : pVal, second = clOverride ? preferred : cVal;

        if (preferred != null && !preferred.isEmpty())
            return preferred;
        if (second != null && !second.isEmpty())
            return second;
        return def;
    }

    private static void mkdirs(Path dir) {
        if (!Files.exists(dir)) {
            Expect.compute(() -> Files.createDirectories(dir))
                    .ifCaught(ex -> {
                        throw new RuntimeException(String.format("Directory %s cannot be created ", dir), ex);
                    });
        }
    }

    @SneakyThrows
    private static YamlConfiguration loadConfig() {
        YamlSerializer serializer = YamlSerializer.getInstance();

        var conf = dataDir.resolve("config.yml");
        if (!Files.exists(conf)) {
            try (var in = getCurrentConfig();
                 var out = Files.newOutputStream(conf)) {
                IOUtil.write(in, out).rethrow();
            }
            try (var reader = Files.newBufferedReader(conf)) {
                return serializer.deserialize(reader);
            }
        }
        YamlConfiguration current, saved;
        try (var in = new InputStreamReader(getCurrentConfig());
             var reader = Files.newBufferedReader(conf)) {
            current = serializer.deserialize(in);
            saved = serializer.deserialize(reader);
        }

        // check if upgrade is required
        if (current.map(saved, "config-version", Configuration::int32, Conditions::lte).orElse(false))
            return saved;

        // upgrade config.yml with newer contents
        saved.set("config-version", current.int32("config-version").orElseThrow());
        saved.converge(current, true, true);
        saved.purge(true);

        // finally, save new config
        saved.dumpTo(conf).rethrow();
        return saved;
    }

    private static InputStream getCurrentConfig() {
        return Objects.requireNonNull(Internal.class.getResourceAsStream("/META-INF/coreconfig.yml"), "Configuration not found");
    }

    // - Init methods -

    /**
     * Init point of the library. Invoked by the loader.
     */
    @Synchronized
    public static void agentInit(Instrumentation instrumentation) {
        if (Internal.instrumentation != null)
            throw new IllegalStateException();
        Internal.instrumentation = Objects.requireNonNull(instrumentation);

        AccessibleObjectTransformer.acquirePrivileges();
        Unsafe.doPrivileged(Internal::widenModuleAccess);
        PropertiesLock.lockSystemProperties(AccessibleObjectTransformer.SUDO_KEY, InjectionManager.HOOKING_KEY, InjectionManager.PROXY_KEY);
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

    // - utility methods for the library -

    public static boolean isUlibClass(Class<?> cl) {
        Module m;
        if (!(m = cl.getModule()).isNamed())
            return false; // ulib classes are all in named modules

        // check if the module is a genuine ulib module
        if (!m.getName().startsWith("ulib.")
            || m != Internal.class.getModule().getLayer().findModule(m.getName()).orElse(null))
            return false;

        // name check should be unnecessary, buh eh
        return cl.getName().startsWith("eu.software4you.ulib.");
    }

    /**
     * <b>WARNING</b> This method does not check if the class itself is a ulib class.
     * <p>
     * A class is considered an implementation class if it is not opened or exported.
     */
    public static boolean isImplClass(Class<?> cl) {
        var m = cl.getModule();
        var pack = cl.getPackageName();
        return !m.isExported(pack) && !m.isOpen(pack);
    }

    // sudo

    public static boolean isSudoThread() {
        return sudoThreads.contains(Thread.currentThread());
    }

    public static <T, X extends Exception> T sudo(Func<T, X> task) throws X {
        if (isSudoThread())
            return task.execute();

        final var thr = Thread.currentThread();
        sudoThreads.add(thr);
        try {
            return task.execute();
        } finally {
            sudoThreads.remove(thr);
        }
    }

    public static void makeAccessibleTo(Module other) {
        var noAccessYet = Internal.class.getModule().getLayer().modules().stream()
                // grab ulib modules
                .filter(m -> m.getName().startsWith("ulib."))
                // filter out the modules `other` can already read
                .filter(api -> !other.canRead(api))
                .toList();
        if (noAccessYet.isEmpty())
            return;

        Unsafe.doPrivileged(() -> {
            for (Module api : noAccessYet) {
                ReflectUtil.icall(other, "implAddReads()", Param.single(Module.class, api));
            }
        });
    }
}
