package eu.software4you.ulib;

import eu.software4you.common.Nameable;
import eu.software4you.common.collection.Pair;
import eu.software4you.configuration.yaml.YamlSub;
import eu.software4you.ulib.impl.configuration.yaml.YamlSerializer;
import eu.software4you.utils.IOUtil;
import lombok.SneakyThrows;
import lombok.val;
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
    final Level LOG_LEVEL;
    final String BRAND;
    final List<Pair<String, String>> ADDITIONAL_LIBS = new ArrayList<>();
    final boolean FORCE_SYNC;

    private final boolean clOverride;
    private YamlSub yaml;

    @SneakyThrows
    private Properties() {
        BRAND = "\n" +
                "        ______ _____ ______  \n" +
                "____  _____  / ___(_)___  /_ \n" +
                "_  / / /__  /  __  / __  __ \\\n" +
                "/ /_/ / _  /____  /  _  /_/ /\n" +
                "\\__,_/  /_____//_/   /_.___/ \n" +
                "                             ";

        DATA_DIR = new File(System.getProperty("ulib.directory.main", ".ulib"));
        if (!DATA_DIR.exists()) {
            if (!DATA_DIR.mkdirs())
                throw new Exception(String.format("Data directory cannot be created (%s)", DATA_DIR));
        }

        yaml = loadConfig();
        clOverride = yaml.get("override-command-line", false);

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
    private YamlSub loadConfig() {
        YamlSerializer serializer = YamlSerializer.getInstance();

        val conf = new File(DATA_DIR, "config.yml");
        if (!conf.exists()) {
            IOUtil.write(getCurrentConfig(), new FileOutputStream(conf));
            return serializer.deserialize(new FileReader(conf));
        }
        YamlSub current = serializer.deserialize(new InputStreamReader(getCurrentConfig()));
        YamlSub saved = serializer.deserialize(new FileReader(conf));

        // check if upgrade is required
        int cv = current.get("config-version", 1), sv = saved.get("config-version", 0);
        if (sv == cv)
            return saved;

        System.out.printf("[uLib] Upgrading config from version %d to %d%n", sv, cv);

        // upgrade config.yml with newer contents
        YamlSub upgrade = serializer.createNew();
        upgrade.set("config-version", cv);
        upgrade.setComments("config-version", current.getComments("config-version"));


        current.getValues(true).forEach((k, v) -> {
            if (k.equals("config-version"))
                return; // config-version already set, skip
            if (!saved.isSet(k)) {
                upgrade.set(k, v);
                upgrade.setComments(k, current.getComments(k));
            } else { // config.yml already contains key
                upgrade.set(k, saved.get(k));
                upgrade.setComments(k, saved.getComments(k));
            }
        });
        // set comments of subs (bc the subs itself arent included in #getValues())
        copyComments(saved, upgrade);

        upgrade.save(new FileWriter(conf));
        return upgrade;
    }

    private void copyComments(YamlSub source, YamlSub target) {
        source.getSubs().forEach(sub -> {
            String key = ((Nameable) sub).getName(); // #getName *is* the key

            //noinspection ConstantConditions
            target.setComments(key, source.getComments(key));

            // deep
            copyComments(sub, target.getSub(key));
        });
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
