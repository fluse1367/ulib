package eu.software4you.ulib;

import eu.software4you.aether.MavenRepository;
import eu.software4you.aether.UnsafeLibraries;
import eu.software4you.utils.ClassUtils;

import java.io.*;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.*;

public class ULib implements Lib {

    private static final Lib instance = new ULib();

    private final Logger logger;
    private final Properties properties;
    private final String version;
    private final RunMode runMode;
    private final String name;
    private final String nameOnly;
    private boolean init = false;

    private ULib() {
        properties = Properties.getInstance();
        version = ULib.class.getPackage().getImplementationVersion();
        runMode = ClassUtils.isClass("eu.software4you.ulib.spigotbungeecord.bridge.SpigotSBB") ?
                RunMode.SPIGOT : (ClassUtils.isClass("eu.software4you.ulib.spigotbungeecord.bridge.BungeeCordSBB") ? RunMode.BUNGEECORD : RunMode.STANDALONE);
        nameOnly = "uLib";
        name = String.format("%s-%s", nameOnly, runMode.getName());


        logger = Logger.getLogger(getClass().getName());
        logger.setUseParentHandlers(false);
    }

    public static Lib getInstance() {
        return instance;
    }

    public static void makeReady() {
        instance.init();
    }

    @Override
    public void init() {
        if (init)
            return;
        init = true;

        if (!properties.QUIET) {
            System.out.println(properties.BRAND);
            System.out.printf("uLib by software4you.eu, running %s implementation version %s%n", runMode.getName(), version);
            System.out.println("Log level: " + properties.LOG_LEVEL);
            System.out.println("This uLib log file will be placed in: " + properties.DATA_DIR);
        }

        prepareLogger(logger);

        long started = System.currentTimeMillis();
        info("Startup ...");

        // (down-)loading dependencies from maven central
        try {
            UnsafeLibraries libs = new UnsafeLibraries(getLogger(), String.format("%s/%s", nameOnly, version));

            debug("Preparing for aether ...");

            libs.require("org.apache.commons:commons-lang3:3.8.1", "org.apache.commons.lang3.StringUtils");
            libs.require("com.google.guava:guava:27.0.1-jre", "com.google.common.base.Objects");
            libs.require("commons-logging:commons-logging:1.2", "org.apache.commons.logging.Log");


            debug("Loading aether components ...");

            libs.require("org.eclipse.aether:aether-api:1.1.0", "org.eclipse.aether.RepositorySystem");
            libs.require("org.eclipse.aether:aether-util:1.1.0", "org.eclipse.aether.util.StringUtils");
            libs.require("org.eclipse.aether:aether-spi:1.1.0", "org.eclipse.aether.spi.connector.transport.Transporter");
            libs.require("org.eclipse.aether:aether-impl:1.1.0", "org.eclipse.aether.impl.Installer");
            libs.require("org.eclipse.aether:aether-connector-basic:1.1.0", "org.eclipse.aether.connector.basic.ChecksumCalculator");
            libs.require("org.eclipse.aether:aether-transport-file:1.1.0", "org.eclipse.aether.transport.file.FileTransporter");

            libs.require("commons-codec:commons-codec:1.11", "org.apache.commons.codec.Decoder");
            libs.require("org.apache.httpcomponents:httpcore:4.3.2", "org.apache.http.HttpStatus");
            libs.require("org.apache.httpcomponents:httpclient:4.3.5", "org.apache.http.client.HttpClient");
            libs.require("org.eclipse.aether:aether-transport-http:1.1.0", "org.eclipse.aether.transport.http.HttpTransporter");

            libs.require("org.codehaus.plexus:plexus-utils:3.0.22", "org.codehaus.plexus.util.Scanner");
            libs.require("org.apache.maven.wagon:wagon-provider-api:1.0", "org.apache.maven.wagon.Wagon");
            libs.require("org.eclipse.aether:aether-transport-wagon:1.1.0", "org.eclipse.aether.transport.wagon.WagonTransporter");

            libs.require("org.apache.maven:maven-repository-metadata:3.3.9", "org.apache.maven.artifact.repository.metadata.Metadata");
            libs.require("org.codehaus.plexus:plexus-interpolation:1.21", "org.codehaus.plexus.interpolation.Interpolator");
            libs.require("org.codehaus.plexus:plexus-component-annotations:1.6", "org.codehaus.plexus.component.annotations.Component");
            libs.require("org.apache.maven:maven-model:3.3.9", "org.apache.maven.model.Model");
            libs.require("org.apache.maven:maven-builder-support:3.3.9", "org.apache.maven.building.Source");
            libs.require("org.apache.maven:maven-artifact:3.3.9", "org.apache.maven.artifact.Artifact");
            libs.require("org.apache.maven:maven-model-builder:3.3.9", "org.apache.maven.model.building.ModelBuilder");
            libs.require("org.apache.maven:maven-aether-provider:3.3.9", "org.apache.maven.repository.internal.MavenWorkspaceReader");

            // aether is now loaded with http capabilities

            MavenRepository.requireLibrary("org.apache.maven.wagon:wagon-ssh:3.3.4", "org.apache.maven.wagon.providers.ssh.jsch.SftpWagon");

            debug("Loading libraries ...");

            MavenRepository.requireLibrary("org.apache.commons:commons-io:1.3.2", "org.apache.commons.io.IOUtils");
            MavenRepository.requireLibrary("com.fasterxml.jackson.core:jackson-core:2.9.8", "com.fasterxml.jackson.core.JsonParser");
            MavenRepository.requireLibrary("com.fasterxml.jackson.core:jackson-databind:2.9.8", "com.fasterxml.jackson.databind.JsonSerializable");
            MavenRepository.requireLibrary("com.github.oshi:oshi-core:4.2.0", "oshi.SystemInfo");
            MavenRepository.requireLibrary("org.yaml:snakeyaml:1.23", "org.yaml.snakeyaml.Yaml");
            MavenRepository.requireLibrary("mysql:mysql-connector-java:8.0.13", "com.mysql.cj.MysqlConnection");
            MavenRepository.requireLibrary("commons-lang:commons-lang:2.6", "org.apache.commons.lang.Validate");
            MavenRepository.requireLibrary("org.xerial:sqlite-jdbc:3.25.2", "org.sqlite.core.Codes");
            MavenRepository.requireLibrary("org.apache.commons:commons-configuration2:2.4", "org.apache.commons.configuration2.Configuration");
            MavenRepository.requireLibrary("commons-beanutils:commons-beanutils:1.9.3", "org.apache.commons.beanutils.BeanUtils");
            MavenRepository.requireLibrary("com.sun.mail:smtp:1.6.3", "com.sun.mail.smtp.SMTPMessage");
            MavenRepository.requireLibrary("javax.mail:javax.mail-api:1.6.2", "javax.mail.Message");
            MavenRepository.requireLibrary("net.sf.jopt-simple:jopt-simple:6.0-alpha-3", "joptsimple.OptionParser");
            MavenRepository.requireLibrary("com.google.code.gson:gson:2.8.6", "com.google.gson.Gson");

            if (!properties.ADDITIONAL_LIBS.isEmpty()) {
                debug("Loading additional libraries ...");

                for (Map.Entry<String, String> en : properties.ADDITIONAL_LIBS.entrySet()) {
                    MavenRepository.requireLibrary(en.getKey(), en.getValue());
                }
            }


        } catch (Exception e) {
            exception(e, "Error while loading dependencies. You might experiencing issues.");
        }

        info(String.format("Startup done (%ss)!", BigDecimal.valueOf(System.currentTimeMillis() - started)
                .divide(BigDecimal.valueOf(1000), new MathContext(2, RoundingMode.HALF_UP)).toPlainString()
        ));
    }

    private void prepareLogger(Logger logger) {
        Function<Throwable, String> stackTraceGetter = throwable -> {
            StringWriter wr = new StringWriter();
            throwable.printStackTrace(new PrintWriter(wr));
            return wr.toString();
        };
        // init logger
        PrintStream err = System.err;
        // make ConsoleHandler use the actual stderr
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err)));
        ConsoleHandler consoleHandler = new ConsoleHandler();
        // reset System.err to previous one
        System.setErr(err);

        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder b = new StringBuilder(String.format("[%s] %tT %s: %s\n",
                        nameOnly, new Date(record.getMillis()), record.getLevel().getName(), record.getMessage()));
                if (record.getThrown() != null)
                    b.append(stackTraceGetter.apply(record.getThrown())).append("\n");
                return b.toString();
            }
        });
        consoleHandler.setLevel(properties.LOG_LEVEL);
        logger.addHandler(consoleHandler);
        try {
            FileHandler fileHandler = new FileHandler(properties.DATA_DIR.getPath() + "/ulib.%g.log",
                    67108864 /*64 MiB*/, 16);
            fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
                    // trim ansi
                    // https://stackoverflow.com/questions/25189651/how-to-remove-ansi-control-chars-vt100-from-a-java-string#25189932
                    return consoleHandler.getFormatter().format(record).replaceAll("\u001B\\[[\\d;]*[^\\d;]", "");
                }
            });
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not append the file handler to the logger. All uLib logged records will not be saved to disk.");
        }
        logger.setLevel(Level.ALL);
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
