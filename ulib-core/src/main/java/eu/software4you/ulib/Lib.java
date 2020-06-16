package eu.software4you.ulib;

import java.io.File;
import java.util.logging.Logger;

public interface Lib {
    void init();

    Logger getLogger();

    RunMode getMode();

    String getVersion();

    String getName();

    String getNameOnly();

    File getDataDir();

    File getLibsM2dir();

    void debug(String debug);

    void debugImplementation(String what);

    void info(String info);

    void warn(String warn);

    void error(String error);

    void exception(Exception exception);

    void exception(Exception exception, String message);
}
