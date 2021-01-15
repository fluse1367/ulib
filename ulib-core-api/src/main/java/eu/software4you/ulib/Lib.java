package eu.software4you.ulib;

import java.io.File;
import java.util.logging.Logger;

public interface Lib {

    RunMode getMode();

    String getVersion();

    String getName();

    String getNameOnly();

    File getDataDir();

    File getLibsM2Dir();

    File getLibsUnsafeDir();

    Logger getLogger();

    void debug(String debug);

    void debugImplementation(String what);

    void info(String info);

    void warn(String warn);

    void error(String error);

    void exception(Throwable throwable);

    void exception(Throwable throwable, String message);
}
