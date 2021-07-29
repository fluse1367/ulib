package eu.software4you.ulib;

import eu.software4you.common.Keyable;
import eu.software4you.common.collection.Pair;
import eu.software4you.configuration.yaml.YamlSub;
import eu.software4you.io.IOUtil;
import eu.software4you.ulib.impl.configuration.yaml.YamlSerializer;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

class Properties {
    private static final Properties instance = new Properties();

    final File DATA_DIR;
    final File LOGS_DIR;
    final File CACHE_DIR;
    final File LIBS_UNSAFE_DIR;
    final File LIBS_DIR;
    final boolean QUIET;
    final boolean NO_SPLASH;
    final boolean UNSAFE_OPERATIONS;
    final Level LOG_LEVEL;
    final String BRAND;
    final List<Pair<String, String>> ADDITIONAL_LIBS = new ArrayList<>();
    final boolean FORCE_SYNC;

    private final boolean clOverride;
    private YamlSub yaml;

    RunMode MODE = RunMode.STANDALONE;

    @SneakyThrows
    private Properties() {
        BRAND = """

                        ______ _____ ______ \s
                ____  _____  / ___(_)___  /_\s
                _  / / /__  /  __  / __  __ \\
                / /_/ / _  /____  /  _  /_/ /
                \\__,_/  /_____//_/   /_.___/\s
                                            \s""";

        DATA_DIR = new File(System.getProperty("ulib.directory.main", ".ulib"));
        mkdirs(DATA_DIR);

        yaml = loadConfig();
        clOverride = yaml.get("override-command-line", false);

        LOGS_DIR = new File(get("directories.logs", "ulib.directory.logs",
                String.format("%s%slogs", DATA_DIR.getPath(), File.separator)));
        mkdirs(LOGS_DIR);

        CACHE_DIR = new File(get("directories.cache", "ulib.directory.cache",
                String.format("%s%scache", DATA_DIR.getPath(), File.separator)));
        mkdirs(CACHE_DIR);

        LIBS_UNSAFE_DIR = new File(get("directories.libraries_unsafe", "ulib.directory.libraries_unsafe",
                String.format("%s%slibraries_unsafe", CACHE_DIR.getPath(), File.separator)));
        mkdirs(LIBS_UNSAFE_DIR);

        LIBS_DIR = new File(get("directories.libraries", "ulib.directory.libraries",
                String.format("%s%slibraries", DATA_DIR.getPath(), File.separator)));
        mkdirs(LIBS_DIR);


        QUIET = get("logging.quiet", "ulib.quiet", "false").equalsIgnoreCase("true");
        NO_SPLASH = get("logging.splash", "ulib.splash", "true").equalsIgnoreCase("false");

        Level logLevel = Level.INFO;
        try {
            String levelStr = get("logging.log-level", "ulib.loglevel", "INFO").toUpperCase();
            logLevel = levelStr.equals("DEBUG") ? Level.FINEST : Level.parse(levelStr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        LOG_LEVEL = QUIET ? Level.OFF : logLevel;

        FORCE_SYNC = get("force-synchronous-work", "ulib.forcesync", "false").equalsIgnoreCase("true");
        UNSAFE_OPERATIONS = get("unsafe-operations", "ulib.unsafe_operations", "deny").equalsIgnoreCase("allow");

        yaml = null; // let gc do its work
    }

    @SneakyThrows
    private YamlSub loadConfig() {
        YamlSerializer serializer = YamlSerializer.getInstance();

        var conf = new File(DATA_DIR, "config.yml");
        if (!conf.exists()) {
            IOUtil.write(getCurrentConfig(), new FileOutputStream(conf));
            return serializer.deserialize(new FileReader(conf));
        }
        YamlSub current = serializer.deserialize(new InputStreamReader(getCurrentConfig()));
        YamlSub saved = serializer.deserialize(new FileReader(conf));

        // check if upgrade is required
        int cv = current.get("config-version", -1), sv = saved.get("config-version", -2);
        if (cv <= sv)
            return saved;

        System.out.printf("[uLib] Upgrading config from version %d to %d%n", sv, cv);

        // upgrade config.yml with newer contents
        saved.set("config-version", cv);
        current.getValues(true).forEach((k, v) -> {
            if (!saved.isSet(k)) {
                saved.set(k, v);
                saved.setComments(k, current.getComments(k));
            }
        });

        saved.save(new FileWriter(conf));
        return saved;
    }

    private void copyComments(YamlSub source, YamlSub target) {
        source.getSubs().forEach(sub -> {
            String key = ((Keyable<String>) sub).getKey();

            //noinspection ConstantConditions
            target.setComments(key, source.getComments(key));

            // deep
            copyComments(sub, target.getSub(key));
        });
    }

    private InputStream getCurrentConfig() {
        return Validate.notNull(getClass().getResourceAsStream("/coreconfig.yml"), "Configuration not found");
    }

    private String get(String yamlPath, String propsKey, String def) {
        String pVal = System.getProperty(propsKey), cVal = String.valueOf(yaml.get(yamlPath, pVal));
        String preferred = clOverride ? cVal : pVal, second = clOverride ? preferred : cVal;

        if (preferred != null && !preferred.isEmpty())
            return preferred;
        if (second != null && !second.isEmpty())
            return second;
        return def;
    }

    @SneakyThrows
    private void mkdirs(File dir) {
        if (!dir.exists()) {
            if (!dir.mkdirs())
                throw new Exception(String.format("Directory %s cannot be created ", dir));
        }
    }

    static Properties getInstance() {
        return instance;
    }
}
