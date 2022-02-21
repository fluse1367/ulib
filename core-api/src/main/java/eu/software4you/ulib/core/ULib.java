package eu.software4you.ulib.core;

import eu.software4you.ulib.core.api.Lib;
import lombok.SneakyThrows;

import java.util.logging.Logger;

/**
 * Loading this class will cause the library initialization.
 */
public final class ULib {

    private static final Lib impl;

    static {
        impl = load();
        postLoad();
    }

    @SneakyThrows
    private static Lib load() {
        var loader = ULib.class.getModule().getLayer().findModule("ulib.core")
                .map(Module::getClassLoader)
                .orElseThrow(IllegalStateException::new);
        return (Lib) Class.forName("eu.software4you.ulib.core.impl.LibImpl", true, loader)
                .getConstructor().newInstance();
    }

    @SneakyThrows
    private static void postLoad() {
        impl.getClass().getMethod("postInit").invoke(impl);
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
}
