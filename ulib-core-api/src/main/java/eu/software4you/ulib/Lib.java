package eu.software4you.ulib;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Logger;

/**
 * Represents the core instance of the library.
 */
public interface Lib {
    /**
     * Returns the implementation version.
     */
    @NotNull
    RunMode getMode();

    /**
     * Returns the implementation version string.
     */
    @NotNull
    String getVersion();

    /**
     * Returns the operation name of the library.<br>
     * It is in format "name-version" (e.g. "uLib-standalone")
     */
    @NotNull
    String getName();

    /**
     * Returns the name of the library. Currently always returns "uLib"
     *
     * @return "uLib"
     */
    @NotNull
    String getNameOnly();

    /**
     * Returns the data directory.<br>
     * Usually this is ".ulib".
     */
    @NotNull
    File getDataDir();

    /**
     * Returns the local maven repository directory.<br>
     * Usually this is ".ulib/libraries"
     */
    @NotNull
    File getLibrariesDir();

    /**
     * Returns the local unsafe repository directory.
     * Usually this is ".ulib/libraries_unsafe"
     */
    @NotNull
    File getLibrariesUnsafeDir();

    /**
     * Returns the main logger instance of the library.
     */
    @NotNull
    Logger getLogger();
}
