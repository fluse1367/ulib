package eu.software4you.ulib.core.impl;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.Lib;
import eu.software4you.ulib.core.api.RunMode;
import eu.software4you.ulib.core.api.dependencies.Dependencies;
import eu.software4you.ulib.core.api.dependencies.Repositories;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LibImpl implements Lib {

    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();
    final static long MAIN_THREAD_ID;
    private static Logger logger;

    static {
        MAIN_THREAD_ID = Thread.currentThread().getId();
        clinit();
    }

    private static void clinit() {
        long started = System.currentTimeMillis();

        // init agent
        Agent.init();
        if (!Agent.available()) {
            throw new IllegalStateException("Agent not loaded");
        }

        var lib = new LibImpl();

        logger.info(() -> "Log level: " + lib.properties.LOG_LEVEL);
        logger.fine(() -> String.format("Thread ID is %s", MAIN_THREAD_ID));
        logger.info(() -> "Loading ...");

        // load dependencies
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

        if (Properties.getInstance().FORCE_SYNC) {
            logger.warning("Enforcing synchronous work enabled. This will significantly decrease uLib's performance in certain areas!");
        }

        if (UnsafeOperations.allowed()) {
            logger.warning(() -> "Unsafe operations are allowed. " +
                                 "Be aware that allowing unsafe operations is potentially dangerous and can lead to instability and/or damage of any kind! " +
                                 "Use this at your own risk!");
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

    @SuppressWarnings("unchecked")
    @Override
    public <S> S getService(Class<S> service) throws IllegalArgumentException {
        if (!SERVICES.containsKey(service)) {
            var loader = ServiceLoader.load(service);
            var first = loader.findFirst();
            if (first.isEmpty()) {
                throw new IllegalArgumentException("No service provider found for " + service.getName());
            }
            SERVICES.put(service, first.get());
        }

        return (S) SERVICES.get(service);
    }
}
