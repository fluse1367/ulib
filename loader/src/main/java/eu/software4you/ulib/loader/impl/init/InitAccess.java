package eu.software4you.ulib.loader.impl.init;

import eu.software4you.ulib.loader.impl.install.ModuleClassProvider;
import eu.software4you.ulib.loader.install.Installer;
import lombok.Synchronized;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.List;

public class InitAccess {

    private static final InitAccess inst = new InitAccess();

    public static InitAccess getInstance() {
        if (StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass() != Installer.class)
            throw new SecurityException();
        return inst;
    }


    private Object initializer, injector;

    private boolean init;

    public void install(ClassLoader cl, Module publish) throws ReflectiveOperationException {
        try {
            init();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        injector.getClass().getMethod("installLoaders", ClassLoader.class)
                .invoke(injector, cl);

        if (publish != null)
            injector.getClass().getMethod("addReadsTo", Module.class)
                    .invoke(injector, publish);
    }

    public ModuleLayer layer() {
        try {
            return (ModuleLayer) initializer.getClass().getMethod("getLayer").invoke(initializer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    @Synchronized
    private void init() throws URISyntaxException, ReflectiveOperationException {
        if (init)
            return;
        init = true;

        if (getClass().getModule().isNamed()) {
            // loader already loaded as module
            var initializer = Initializer.provide();
            this.initializer = initializer;
            this.injector = initializer.getInjector();
            return;
        }

        // load self as module
        File me = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

        var provider = new ModuleClassProvider(null, List.of(me),
                ClassLoader.getSystemClassLoader(), ModuleLayer.boot(), true);

        var loader = provider.getLayer().findLoader("ulib.loader");

        this.initializer = Class.forName("eu.software4you.ulib.loader.impl.init.Initializer", true, loader)
                .getMethod("provide")
                .invoke(null);

        this.injector = initializer.getClass().getMethod("getInjector").invoke(this.initializer);
    }

}
