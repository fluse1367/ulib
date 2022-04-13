package eu.software4you.ulib.loader.install;

import eu.software4you.ulib.loader.impl.init.InitAccess;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Installer {

    private static final InitAccess access = InitAccess.getInstance();

    /**
     * Installs the uLib API to a class loader by injecting code into it.
     *
     * @param target the class loader to install the API to
     * @throws IllegalArgumentException If the uLib API has already been installed to that class loader
     * @implNote If the target is the loader of a named module, an {@link Module#addReads(Module) reads} record must be
     * added by that module manually in order for that module to be able to interact with the uLib API.
     * @see #getLayer()
     */
    @Synchronized
    public static void installTo(ClassLoader target) {
        try {
            access.install(target, null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Install the uLib API to the class loader of the calling class.
     *
     * @implNote Unlike {@link #installTo(ClassLoader)}, this method will automatically add
     * an {@link Module#addReads(Module) reads} record to the module of the calling class (if necessary).
     */
    @Synchronized
    public static void installMe() {
        var caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        var loader = caller.getClassLoader();
        try {
            access.install(loader, caller.getModule());
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the module layer object from the uLib api.
     *
     * @return the module layer object
     */
    public static ModuleLayer getLayer() {
        return access.layer();
    }
}
