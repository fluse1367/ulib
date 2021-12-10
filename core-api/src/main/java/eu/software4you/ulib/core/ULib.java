package eu.software4you.ulib.core;

import eu.software4you.ulib.core.api.Lib;

import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Loading this class will cause the library initialization.
 */
public final class ULib {

    private static final Lib impl;

    // clinit
    static {
        var loader = ServiceLoader.load(Lib.class);
        var first = loader.findFirst();
        if (first.isEmpty())
            throw new IllegalStateException();
        impl = first.get();
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
     * Returns a singleton service.
     *
     * @param service the service base interface / class
     * @param <S>     the service type
     * @return the service
     * @throws IllegalArgumentException if no service is loaded for the specified class
     */
    public static <S> S service(Class<S> service) throws IllegalArgumentException {
        return impl.getService(service);
    }

    /**
     * Returns a singleton service provider.
     *
     * @param service the service base interface / class
     * @param <S>     the service type
     * @return the service provider
     * @throws IllegalArgumentException if no service is loaded for the specified class
     */
    public static <S> ServiceLoader.Provider<S> provider(Class<S> service) throws IllegalArgumentException {
        return impl.getProvider(service);
    }

    /**
     * Used to load this class. Alternatively you can use {@link Class#forName(String)}.
     */
    public static void init() {
    }
}
