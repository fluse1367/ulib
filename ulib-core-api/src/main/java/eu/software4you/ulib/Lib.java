package eu.software4you.ulib;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.logging.Level;
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
     * Usually this is ".ulib/m2"
     */
    @NotNull
    File getLibsM2Dir();

    /**
     * Returns the local unsafe repository directory.
     * Usually this is ".ulib/unsafe"
     */
    @NotNull
    File getLibsUnsafeDir();

    /**
     * Returns the main logger instance of the library.
     */
    @NotNull
    Logger getLogger();

    /**
     * Logs a {@link java.util.logging.Level#FINE} record.
     *
     * @param debug the message
     * @see #getLogger()
     * @see Logger#log(Level, String)
     */
    void debug(@NotNull String debug);

    /**
     * Logs a {@link java.util.logging.Level#INFO} record.
     *
     * @param info the message
     * @see #getLogger()
     * @see Logger#log(Level, String)
     */
    void info(@NotNull String info);

    /**
     * Logs a {@link java.util.logging.Level#WARNING} record.
     *
     * @param warn the message
     * @see #getLogger()
     * @see Logger#log(Level, String)
     */
    void warn(@NotNull String warn);

    /**
     * Logs a {@link java.util.logging.Level#SEVERE} record.
     *
     * @param error the message
     * @see #getLogger()
     * @see Logger#log(Level, String)
     */
    void error(@NotNull String error);

    /**
     * Logs a {@link java.util.logging.Level#SEVERE} record.
     *
     * @param throwable the error occurred
     * @see #getLogger()
     * @see Logger#log(Level, String, Throwable)
     */
    void exception(@NotNull Throwable throwable);

    /**
     * Logs a {@link java.util.logging.Level#SEVERE} record.
     *
     * @param throwable the error occurred
     * @param message   the message
     * @see #getLogger()
     * @see Logger#log(Level, String, Throwable)
     */
    void exception(@NotNull Throwable throwable, @NotNull String message);
}
