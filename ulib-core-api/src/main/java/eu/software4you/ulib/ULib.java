package eu.software4you.ulib;

/**
 * Loading this class will cause the library initialization.
 */
public final class ULib {

    private static final Lib impl;

    static {
        try {
            Class.forName("eu.software4you.ulib.LibImpl");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("No implementation", e);
        }
        impl = ImplRegistry.get(Lib.class);
    }

    public static Lib getInstance() {
        return impl != null ? impl : ImplRegistry.get(Lib.class);
    }

    /**
     * Used to load this class. Alternatively you can use {@link Class#forName(String)}.
     */
    public static void init() {
    }
}
