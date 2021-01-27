package eu.software4you.ulib;

/**
 * Loading this class will cause the library initialization.
 */
public final class ULib {

    static Lib impl;

    static {
        try {
            Class.forName("eu.software4you.ulib.LibImpl");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No implementation", e);
        }
    }

    public static Lib get() {
        return impl;
    }

    /**
     * Used to load this class. Alternatively you can use {@link Class#forName(String)}.
     */
    public static void init() {
    }
}
