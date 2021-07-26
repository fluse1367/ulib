package eu.software4you.ulib;

import eu.software4you.dependencies.Dependencies;
import eu.software4you.dependencies.Repositories;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.utils.FileUtils;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

final class LibImpl implements Lib {
    final static long MAIN_THREAD_ID;
    static Logger logger;

    static {
        MAIN_THREAD_ID = Thread.currentThread().getId();
        clinit();
    }

    private static void clinit() {
        long started = System.currentTimeMillis();

        var lib = new LibImpl();
        System.out.println("This uLib log file will be placed in: " + lib.properties.DATA_DIR);
        ULib.impl = lib;
        logger.info(() -> "Log level: " + lib.properties.LOG_LEVEL);
        logger.fine(() -> String.format("Thread ID is %s", MAIN_THREAD_ID));
        logger.info(() -> "Loading ...");

        ImplInjector.logger = logger;
        AgentInstaller.install(logger);

        logger.fine(() -> "Preparing implementations");
        var IMPL = clinit_read_implementations();

        /*
         1. ReflectUtil
         2. DependencyLoader
         3. Repositories
         4. Dependencies
            ...
         */

        logger.fine(() -> "Loading implementations");
        try {
            clinit_load_implementations(IMPL);
        } catch (Throwable thr) {
            logger.severe("Error during critical initialization phase (implementation loading).");
            throw thr;
        }

        // load dependencies
        logger.fine(() -> "Loading dependencies");
        try {
            for (var en : lib.properties.ADDITIONAL_LIBS) {
                Dependencies.depend(en.getFirst(), Repositories.of(en.getSecond()));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Error while loading dependencies. You might experience issues.");
        }

        logger.info(() -> String.format("Done (%ss)!", BigDecimal.valueOf(System.currentTimeMillis() - started)
                .divide(BigDecimal.valueOf(1000), new MathContext(4, RoundingMode.HALF_UP)).toPlainString()
        ));

        if (UnsafeOperations.allowed()) {
            logger.warning(() -> "Unsafe operations are allowed. " +
                                 "Be aware that allowing unsafe operations is potentially dangerous and can lead to instability and/or damage of any kind! " +
                                 "Use this at your own risk!");
        }
    }

    @SneakyThrows
    private static List<Class<?>> clinit_read_implementations() {
        List<Class<?>> IMPL = new ArrayList<>();

        // register implementations
        String pack = String.format("%s/impl/", LibImpl.class.getPackage().getName()).replace(".", "/");
        JarFile jar = new JarFile(FileUtils.getClassFile(LibImpl.class));
        var e = jar.entries();

        while (e.hasMoreElements()) {
            JarEntry entry = e.nextElement();
            String name = entry.getName();

            if (name.startsWith(pack) && name.endsWith("Impl.class")) {
                String clName = name.replace("/", ".").substring(0, name.length() - 6);
                // NO class init
                var cl = Class.forName(clName, false, LibImpl.class.getClassLoader());
                if (!cl.isAnnotationPresent(Impl.class))
                    continue;
                Impl impl = cl.getAnnotation(Impl.class);

                logger.finer(() -> String.format("Implementation found: (%d) %s", impl.priority(), cl.getName()));

                IMPL.add(cl);
            }

        }

        // sort implementations by priority (descending)
        IMPL.sort((c1, c2) -> c2.getAnnotation(Impl.class).priority() - c1.getAnnotation(Impl.class).priority());
        return IMPL;
    }

    @SneakyThrows
    private static void clinit_load_implementations(List<Class<?>> IMPL) {
        for (Class<?> clazz : IMPL) {
            var impl = clazz.getAnnotation(Impl.class);
            logger.finer(() -> String.format("Loading implementation: (%d) %s", impl.priority(), clazz.getName()));

            // load dependencies
            var dependencies = impl.dependencies();
            if (dependencies.length > 0) {
                logger.finer(() -> String.format("Implementation %s requires dependencies", clazz.getName()));
                for (String coords : dependencies) {
                    Dependencies.depend(coords);
                }
            }

            // init clazz
            logger.finest(() -> String.format("Initializing implementation %s with %s", clazz.getName(), clazz.getClassLoader().getClass().getName()));
            Class<?> cl = Class.forName(clazz.getName());
            ImplInjector.autoInject(cl);

            logger.finest(() -> String.format("Implementation %s loaded", cl.getName()));
        }
    }

    private final Properties properties;
    private final String version;
    private final RunMode runMode;
    private final String name;
    private final String nameOnly;

    private LibImpl() {
        properties = Properties.getInstance();
        version = ULib.class.getPackage().getImplementationVersion();

        runMode = properties.MODE;

        nameOnly = "uLib";
        name = String.format("%s-%s", nameOnly, runMode.getName());

        if (!properties.NO_SPLASH) {
            System.out.println(properties.BRAND);
            System.out.printf("uLib by software4you.eu, running %s implementation version %s%n", runMode.getName(), version);
        }

        logger = LoggingFactory.fabricate(properties, this);
    }

    @Override
    public @NotNull Logger getLogger() {
        return logger;
    }

    @Override
    @NotNull
    public RunMode getMode() {
        return runMode;
    }

    @Override
    @NotNull
    public String getVersion() {
        return version;
    }

    @Override
    @NotNull
    public String getName() {
        return name;
    }

    @Override
    @NotNull
    public String getNameOnly() {
        return nameOnly;
    }

    @Override
    @NotNull
    public File getDataDir() {
        return properties.DATA_DIR;
    }

    @Override
    @NotNull
    public File getLibrariesDir() {
        return properties.LIBS_DIR;
    }

    @Override
    @NotNull
    public File getLibrariesUnsafeDir() {
        return properties.LIBS_UNSAFE_DIR;
    }

    @Override
    public @NotNull File getCacheDir() {
        return properties.CACHE_DIR;
    }
}
