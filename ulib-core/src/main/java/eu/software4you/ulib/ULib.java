package eu.software4you.ulib;

import eu.software4you.aether.MavenRepository;
import eu.software4you.utils.ClassPathHacker;
import eu.software4you.utils.ClassUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Date;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

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
        runMode = ClassUtils.isClass("eu.software4you.ulib.ULibSpigot") ?
                RunMode.SPIGOT : (ClassUtils.isClass("eu.software4you.ulib.ULibBungeeCord") ? RunMode.BUNGEECORD : RunMode.STANDALONE);
        nameOnly = "uLib";
        name = String.format("%s-%s", nameOnly, runMode.getName());


        logger = Logger.getLogger(name);
        logger.setUseParentHandlers(false);
    }

    public static Lib getInstance() {
        return instance;
    }

    public static void makeReady() {
        instance.init();
    }

    private void requireLibraryUnsafe(String coords, String testClass) throws Exception {
        logger.fine(String.format("Soft-Requiring %s from maven central repo without further dependency resolving", coords));
        if (ClassUtils.isClass(testClass))
            // if this point is reached, the test class is already loaded, which means there is no need to download the library
            return;
        //  we need to download the library and attach it to classpath
        try {
            requireLibraryUnsafe(coords);
            // library successfully required and attached to classpath
        } catch (Exception e) {
            throw new Exception(String.format("An error occurred while loading library %s", coords), e);
        }
        try {
            // check if testClass is accessible (should be at this point)
            Class.forName(testClass);
            // if this point is reached, the test class was successfully downloaded and added to the classpath
        } catch (Exception e) {
            // Class.forName(String) failed (again), library was not loaded (should never happen)
            throw new Exception(String.format("Class %s of library %s was not loaded", testClass, coords), e);
        }
    }

    private void requireLibraryUnsafe(String coords) throws Exception {
        logger.fine(String.format("Requiring %s from maven central repo without further dependency resolving", coords));

        File root = properties.LIBS_UNSAFE_DIR;

        String[] parts = coords.split(":");

        String group = parts[0];
        String name = parts[1];
        String version = parts[2];

        String request = String.format("%s/%s/%s/%s-%s.jar",
                group.replace(".", "/"), name, version, name, version);

        File dest = new File(root, request);

        if (!dest.exists()) {
            dest.getParentFile().mkdirs();

            URL requestURL = new URL("https://repo1.maven.org/maven2/" + request);

            HttpURLConnection conn = (HttpURLConnection) requestURL.openConnection();
            conn.setRequestProperty("User-Agent", this.name + "/" + this.version);
            ReadableByteChannel in = Channels.newChannel(conn.getInputStream());
            FileOutputStream out = new FileOutputStream(dest);
            out.getChannel().transferFrom(in, 0, Long.MAX_VALUE);
            out.close();
            in.close();
        }

        ClassPathHacker.addFile(dest);
    }

    @Override
    public void init() {
        if (init)
            return;
        init = true;

        if (!properties.QUIET) {
            System.out.println(properties.BRAND);
            System.out.println(String.format("uLib by software4you.eu, running %s implementation version %s", runMode.getName(), version));
            System.out.println("Log level: " + properties.LOG_LEVEL);
        }

        // init logger
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(runMode == RunMode.STANDALONE ?
                new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return String.format("[%s] %tT %s: %s\n", nameOnly, new Date(record.getMillis()), record.getLevel().getName(), record.getMessage());
                    }
                }
                :
                new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return String.format("[%s] %s: %s\n", nameOnly, record.getLevel().getName(), record.getMessage());
                    }
                });
        handler.setLevel(properties.LOG_LEVEL);
        logger.addHandler(handler);
        logger.setLevel(properties.LOG_LEVEL);

        long started = System.currentTimeMillis();
        info("Startup ...");

        // (down-)loading dependencies from maven central
        try {
            debug("Preparing for aether ...");

            requireLibraryUnsafe("org.apache.commons:commons-lang3:3.8.1", "org.apache.commons.lang3.StringUtils");
            requireLibraryUnsafe("com.google.guava:guava:27.0.1-jre", "com.google.common.base.Objects");
            requireLibraryUnsafe("commons-logging:commons-logging:1.2", "org.apache.commons.logging.Log");


            debug("Loading aether components ...");

            requireLibraryUnsafe("org.eclipse.aether:aether-api:1.1.0", "org.eclipse.aether.RepositorySystem");
            requireLibraryUnsafe("org.eclipse.aether:aether-util:1.1.0", "org.eclipse.aether.util.StringUtils");
            requireLibraryUnsafe("org.eclipse.aether:aether-spi:1.1.0", "org.eclipse.aether.spi.connector.transport.Transporter");
            requireLibraryUnsafe("org.eclipse.aether:aether-impl:1.1.0", "org.eclipse.aether.impl.Installer");
            requireLibraryUnsafe("org.eclipse.aether:aether-connector-basic:1.1.0", "org.eclipse.aether.connector.basic.ChecksumCalculator");
            requireLibraryUnsafe("org.eclipse.aether:aether-transport-file:1.1.0", "org.eclipse.aether.transport.file.FileTransporter");

            requireLibraryUnsafe("commons-codec:commons-codec:1.11", "org.apache.commons.codec.Decoder");
            requireLibraryUnsafe("org.apache.httpcomponents:httpcore:4.3.2", "org.apache.http.HttpStatus");
            requireLibraryUnsafe("org.apache.httpcomponents:httpclient:4.3.5", "org.apache.http.client.HttpClient");
            requireLibraryUnsafe("org.eclipse.aether:aether-transport-http:1.1.0", "org.eclipse.aether.transport.http.HttpTransporter");

            requireLibraryUnsafe("org.codehaus.plexus:plexus-utils:3.0.22", "org.codehaus.plexus.util.Scanner");
            requireLibraryUnsafe("org.apache.maven.wagon:wagon-provider-api:1.0", "org.apache.maven.wagon.Wagon");
            requireLibraryUnsafe("org.eclipse.aether:aether-transport-wagon:1.1.0", "org.eclipse.aether.transport.wagon.WagonTransporter");

            requireLibraryUnsafe("org.apache.maven:maven-repository-metadata:3.3.9", "org.apache.maven.artifact.repository.metadata.Metadata");
            requireLibraryUnsafe("org.codehaus.plexus:plexus-interpolation:1.21", "org.codehaus.plexus.interpolation.Interpolator");
            requireLibraryUnsafe("org.codehaus.plexus:plexus-component-annotations:1.6", "org.codehaus.plexus.component.annotations.Component");
            requireLibraryUnsafe("org.apache.maven:maven-model:3.3.9", "org.apache.maven.model.Model");
            requireLibraryUnsafe("org.apache.maven:maven-builder-support:3.3.9", "org.apache.maven.building.Source");
            requireLibraryUnsafe("org.apache.maven:maven-artifact:3.3.9", "org.apache.maven.artifact.Artifact");
            requireLibraryUnsafe("org.apache.maven:maven-model-builder:3.3.9", "org.apache.maven.model.building.ModelBuilder");
            requireLibraryUnsafe("org.apache.maven:maven-aether-provider:3.3.9", "org.apache.maven.repository.internal.MavenWorkspaceReader");

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
    public File getLibsM2dir() {
        return properties.LIBS_M2_DIR;
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
    public void exception(Exception e) {
        exception(e, null);
    }

    @Override
    public void exception(Exception e, String msg) {
        if (msg != null && !msg.isEmpty())
            error(msg);
        else
            error("An unexpected exception occurred!");
        e.printStackTrace();
        error("Please contact the support for help.");
    }
}
