package eu.software4you.ulib;

import java.util.logging.Logger;

/**
 * Loading this class will cause the library initialization.
 */
public final class ULib {

    static Lib impl;

    // clinit
    static {
        try {
            Class.forName("eu.software4you.ulib.LibImpl");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No implementation", e);
        }
    }

    /**
     * Returns the library instance.
     *
     * @return the library instance
     * @see Lib
     */
    public static Lib get() {
        return impl;
    }

    /**
     * Returns ulib's {@link Logger} instance.
     *
     * @return the logger instance
     * @see Lib#getLogger()
     * @see Logger
     */
    public static Logger logger() {
        return impl.getLogger();
    }

    /**
     * Used to load this class. Alternatively you can use {@link Class#forName(String)}.
     */
    public static void init() {
    }
}
