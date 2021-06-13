package eu.software4you.ulib;

import eu.software4you.common.collection.Pair;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.Validate;
import ulib.ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
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
    final Level LOG_LEVEL;
    final String BRAND;
    final List<Pair<String, String>> ADDITIONAL_LIBS = new ArrayList<>();
    final boolean FORCE_SYNC;

    private YamlConfiguration yaml;
    private boolean clOverride;

    private Properties() {
        BRAND = "\n" +
                "        ______ _____ ______  \n" +
                "____  _____  / ___(_)___  /_ \n" +
                "_  / / /__  /  __  / __  __ \\\n" +
                "/ /_/ / _  /____  /  _  /_/ /\n" +
                "\\__,_/  /_____//_/   /_.___/ \n" +
                "                             ";

        DATA_DIR = new File(System.getProperty("ulib.directory.main", ".ulib"));

        yaml = loadConfig();
        clOverride = yaml.getBoolean("override-command-line", false);

        LOGS_DIR = new File(get("directories.logs", "ulib.directory.logs",
                String.format("%s%slogs", DATA_DIR.getPath(), File.separator)));
        CACHE_DIR = new File(get("directories.cache", "ulib.directory.cache",
                String.format("%s%scache", DATA_DIR.getPath(), File.separator)));
        LIBS_UNSAFE_DIR = new File(get("directories.libraries_unsafe", "ulib.directory.libraries_unsafe",
                String.format("%s%slibraries_unsafe", CACHE_DIR.getPath(), File.separator)));
        LIBS_DIR = new File(get("directories.libraries", "ulib.directory.libraries",
                String.format("%s%slibraries", DATA_DIR.getPath(), File.separator)));


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

        yaml = null; // let gc do its work
    }

    @SneakyThrows
    private YamlConfiguration loadConfig() {
        val conf = new File(DATA_DIR, "config.yml");
        if (!conf.exists()) {
            IOUtil.write(getCurrentConfig(), new FileOutputStream(conf));
            return YamlConfiguration.loadConfiguration(conf);
        }
        val current = YamlConfiguration.loadConfiguration(new InputStreamReader(getCurrentConfig()));
        val saved = YamlConfiguration.loadConfiguration(conf);

        // check if upgrade is required
        int cv = current.getInt("config-version"), sv = saved.getInt("config-version");
        if (cv > sv) {
            System.out.printf("[uLib] Updating config from version %d to %d%n", sv, cv);
            // update config.yml with newer contents
            current.getValues(true).forEach((k, v) -> {
                if (!saved.isSet(k))
                    saved.set(k, v);
            });
            saved.save(conf);
        }
        return saved;
    }

    private InputStream getCurrentConfig() {
        return Validate.notNull(getClass().getResourceAsStream("/config.yml"), "Configuration not found");
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

    static Properties getInstance() {
        return instance;
    }
}
