package eu.software4you.ulib;

import eu.software4you.aether.MavenRepository;
import eu.software4you.utils.ClassUtils;
import lombok.val;

import java.io.File;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

class Impl implements Init {
    private final Logger logger;
    private final Properties properties;
    private final String version;
    private final RunMode runMode;
    private final String name;
    private final String nameOnly;

    private boolean init = false;

    private Impl() {
        properties = Properties.getInstance();
        version = ULib.class.getPackage().getImplementationVersion();
        runMode = ClassUtils.isClass("eu.software4you.ulib.spigotbungeecord.bridge.SpigotSBB") ?
                RunMode.SPIGOT : (ClassUtils.isClass("eu.software4you.ulib.spigotbungeecord.bridge.BungeeCordSBB") ? RunMode.BUNGEECORD : RunMode.STANDALONE);
        nameOnly = "uLib";
        name = String.format("%s-%s", nameOnly, runMode.getName());


        logger = Logger.getLogger(getClass().getName());
        logger.setUseParentHandlers(false);
    }

    @Override
    public void init() {
        if (init)
            return;
        init = true;

        if (!properties.NO_SPLASH) {
            System.out.println(properties.BRAND);
            System.out.printf("uLib by software4you.eu, running %s implementation version %s%n", runMode.getName(), version);
            System.out.println("Log level: " + properties.LOG_LEVEL);
            System.out.println("This uLib log file will be placed in: " + properties.DATA_DIR);
        }

        LoggingFactory factory = new LoggingFactory(properties, logger, this);
        factory.prepare();
        factory.systemInstall();

        // (down-)loading dependencies
        try {
            if (!properties.ADDITIONAL_LIBS.isEmpty()) {
                long started = System.currentTimeMillis();
                debug("Loading libraries ...");

                for (val en : properties.ADDITIONAL_LIBS.entrySet()) {
                    val v = en.getValue();
                    MavenRepository.requireLibrary(en.getKey(), v.getFirst(), v.getSecond());
                }

                info(String.format("Done (%ss)!", BigDecimal.valueOf(System.currentTimeMillis() - started)
                        .divide(BigDecimal.valueOf(1000), new MathContext(2, RoundingMode.HALF_UP)).toPlainString()
                ));
            }
        } catch (Exception e) {
            exception(e, "Error while loading dependencies. You might experiencing issues.");
        }
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public RunMode getMode() {
        return runMode;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getNameOnly() {
        return nameOnly;
    }

    @Override
    public File getDataDir() {
        return properties.DATA_DIR;
    }

    @Override
    public File getLibsM2Dir() {
        return properties.LIBS_M2_DIR;
    }

    @Override
    public File getLibsUnsafeDir() {
        return properties.LIBS_UNSAFE_DIR;
    }

    @Override
    public void debugImplementation(String what) {
        debug(String.format("Started %s, implementing %s v%s", what, name, getVersion()));
    }

    @Override
    public void debug(String debug) {
        logger.log(Properties.LOG_LEVEL_DEBUG, debug);
    }

    @Override
    public void info(String info) {
        logger.info(info);
    }

    @Override
    public void warn(String warn) {
        logger.warning(warn);
    }

    @Override
    public void error(String error) {
        logger.severe(error);
    }

    @Override
    public void exception(Throwable throwable) {
        exception(throwable, null);
    }

    @Override
    public void exception(Throwable throwable, String msg) {
        if (msg != null && !msg.isEmpty())
            logger.log(Level.SEVERE, msg, throwable);
        else
            logger.log(Level.SEVERE, "An unexpected exception occurred!", throwable);
    }
}
