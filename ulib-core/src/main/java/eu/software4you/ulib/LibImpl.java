package eu.software4you.ulib;

import eu.software4you.dependencies.Dependencies;
import eu.software4you.dependencies.Repositories;
import eu.software4you.utils.ClassUtils;
import eu.software4you.utils.FileUtils;
import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

final class LibImpl implements Lib {
    final static long MAIN_THREAD_ID;

    static {
        long started = System.currentTimeMillis();
        MAIN_THREAD_ID = Thread.currentThread().getId();

        val lib = new LibImpl();
        System.out.println("This uLib log file will be placed in: " + lib.properties.DATA_DIR);
        ULib.impl = lib;
        Logger logger = lib.getLogger();
        logger.info(() -> "Log level: " + lib.properties.LOG_LEVEL);
        logger.fine(() -> String.format("Thread ID is %s", MAIN_THREAD_ID));
        logger.info(() -> "Loading ...");

        ImplInjector.logger = logger;
        AgentInstaller.install(logger);

        // load/register implementations
        try {
            // loading all classes: eu.software4you.ulib.impl.**Impl
            String pack = String.format("%s/impl/", LibImpl.class.getPackage().getName()).replace(".", "/");
            JarFile jar = new JarFile(FileUtils.getClassFile(LibImpl.class));
            val e = jar.entries();

            while (e.hasMoreElements()) {
                JarEntry entry = e.nextElement();
                String name = entry.getName();

                if (name.startsWith(pack) && name.endsWith("Impl.class")) {
                    String clName = name.replace("/", ".").substring(0, name.length() - 6);
                    logger.finer(() -> String.format("Loading implementation %s with %s", clName, LibImpl.class.getClassLoader()));
                    val cl = Class.forName(clName);

                    ImplInjector.autoInject(cl);

                    logger.finer(() -> String.format("Implementation %s loaded", cl.getName()));
                }

            }
        } catch (Throwable e) {
            throw new RuntimeException("Invalid implementation", e);
        }

        // load dependencies
        try {
            for (val en : lib.properties.ADDITIONAL_LIBS) {
                Dependencies.depend(en.getFirst(), Repositories.of(en.getSecond()));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e, () -> "Error while loading dependencies. You might experience issues.");
        }

        logger.info(() -> String.format("Done (%ss)!", BigDecimal.valueOf(System.currentTimeMillis() - started)
                .divide(BigDecimal.valueOf(1000), new MathContext(4, RoundingMode.HALF_UP)).toPlainString()
        ));
    }

    private final Logger logger;
    private final Properties properties;
    private final String version;
    private final RunMode runMode;
    private final String name;
    private final String nameOnly;

    private LibImpl() {
        properties = Properties.getInstance();
        version = ULib.class.getPackage().getImplementationVersion();

        runMode = ClassUtils.isClass("eu.software4you.ulib.ULibVelocityPlugin") ? RunMode.VELOCITY
                : ClassUtils.isClass("eu.software4you.ulib.ULibSpigotPlugin") ? RunMode.SPIGOT
                : ClassUtils.isClass("eu.software4you.ulib.ULibBungeecordPlugin") ? RunMode.BUNGEECORD
                : RunMode.STANDALONE;

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
