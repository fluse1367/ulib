package eu.software4you.ulib;

import eu.software4you.common.collection.Pair;

import java.io.File;
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

    private Properties() {
        BRAND = "\n" +
                "        ______ _____ ______  \n" +
                "____  _____  / ___(_)___  /_ \n" +
                "_  / / /__  /  __  / __  __ \\\n" +
                "/ /_/ / _  /____  /  _  /_/ /\n" +
                "\\__,_/  /_____//_/   /_.___/ \n" +
                "                             ";

        DATA_DIR = new File(System.getProperty("ulib.directory.main", ".ulib"));

        String logsDir = System.getProperty("ulib.directory.logs");
        LOGS_DIR = logsDir != null ? new File(logsDir) : new File(DATA_DIR, "logs");

        String cacheDir = System.getProperty("ulib.directory.cache");
        CACHE_DIR = cacheDir != null ? new File(cacheDir) : new File(DATA_DIR, "cache");

        String libsUnsafeDir = System.getProperty("ulib.directory.libraries_unsafe");
        LIBS_UNSAFE_DIR = libsUnsafeDir != null ? new File(libsUnsafeDir) : new File(CACHE_DIR, "libraries_unsafe");

        String libsDir = System.getProperty("ulib.directory.libraries");
        LIBS_DIR = libsDir != null ? new File(libsDir) : new File(DATA_DIR, "libraries");

        QUIET = System.getProperty("ulib.quiet", "false").equalsIgnoreCase("true");

        NO_SPLASH = System.getProperty("ulib.splash", "true").equalsIgnoreCase("false");

        Level logLevel = Level.INFO;
        try {
            String levelStr = System.getProperty("ulib.loglevel", "INFO").toUpperCase();
            logLevel = levelStr.equals("DEBUG") ? Level.FINEST : Level.parse(levelStr);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        LOG_LEVEL = QUIET ? Level.OFF : logLevel;
    }

    static Properties getInstance() {
        return instance;
    }
}
