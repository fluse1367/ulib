package eu.software4you.ulib;

import eu.software4you.aether.MavenRepository;
import eu.software4you.common.collection.Pair;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

class Properties {
    private static final Properties instance = new Properties();

    final File DATA_DIR;
    final File LIBS_DIR;
    final File LIBS_M2_DIR;
    final File LIBS_UNSAFE_DIR;
    final boolean QUIET;
    final boolean NO_SPLASH;
    final Level LOG_LEVEL;
    final String BRAND;
    final Map<String, Pair<String, MavenRepository.Repository>> ADDITIONAL_LIBS = new HashMap<>();

    private Properties() {
        BRAND = "\n" +
                "        ______ _____ ______  \n" +
                "____  _____  / ___(_)___  /_ \n" +
                "_  / / /__  /  __  / __  __ \\\n" +
                "/ /_/ / _  /____  /  _  /_/ /\n" +
                "\\__,_/  /_____//_/   /_.___/ \n" +
                "                             ";

        DATA_DIR = new File(System.getProperty("eu.software4you.ulib.directory.main", ".ulib"));

        String libsDir = System.getProperty("eu.software4you.ulib.directory.libraries");
        LIBS_DIR = libsDir != null ? new File(libsDir) : new File(DATA_DIR, "libs");

        String libsM2Dir = System.getProperty("eu.software4you.ulib.directory.libraries.m2");
        LIBS_M2_DIR = libsM2Dir != null ? new File(libsM2Dir) : new File(LIBS_DIR, "m2");

        String libsUnsafeDir = System.getProperty("eu.software4you.ulib.directory.libraries.unsafe");
        LIBS_UNSAFE_DIR = libsUnsafeDir != null ? new File(libsUnsafeDir) : new File(LIBS_DIR, "unsafe");

        QUIET = System.getProperty("eu.software4you.ulib.quiet", "false").equalsIgnoreCase("true");

        NO_SPLASH = System.getProperty("eu.software4you.ulib.splash", "true").equalsIgnoreCase("false");

        Level logLevel = Level.INFO;
        try {
            String levelStr = System.getProperty("eu.software4you.ulib.loglevel", "INFO").toUpperCase();
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
