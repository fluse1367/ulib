package eu.software4you.ulib.core;

import eu.software4you.ulib.core.api.Lib;
import lombok.SneakyThrows;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Loading this class will cause the library initialization.
 */
public final class ULib {

    private static final Lib impl = load();
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    @SneakyThrows
    private static Lib load() {
        var loader = ULib.class.getModule().getLayer().findModule("ulib.core")
                .map(Module::getClassLoader)
                .orElseThrow(IllegalStateException::new);
        return (Lib) Class.forName("eu.software4you.ulib.core.impl.LibImpl", true, loader)
                .getConstructor().newInstance();
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
    @SuppressWarnings("unchecked")
    public static <S> S service(Class<S> service) throws IllegalArgumentException {
        if (!SERVICES.containsKey(service)) {
            var loader = ServiceLoader.load(service, ULib.class.getClassLoader());
            var first = loader.findFirst();
            if (first.isEmpty()) {
                throw new IllegalArgumentException("No service provider found for " + service.getName());
            }
            SERVICES.put(service, first.get());
        }

        return (S) SERVICES.get(service);
    }
}
