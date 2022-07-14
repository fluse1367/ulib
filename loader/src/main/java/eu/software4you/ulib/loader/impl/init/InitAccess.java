package eu.software4you.ulib.loader.impl.init;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.Util;
import eu.software4you.ulib.loader.impl.install.ModuleClassProvider;
import eu.software4you.ulib.loader.install.Installer;
import eu.software4you.ulib.loader.minecraft.*;
import lombok.SneakyThrows;
import lombok.Synchronized;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.*;

public class InitAccess {

    private static final Collection<Class<?>> PERMITTED = Util.tryClasses(
            () -> Installer.class,
            () -> PluginVelocity.class,
            () -> PluginSpigot.class,
            () -> PluginBungeecord.class,
            () -> ModFabric.class
    ); // tryClasses bc PluginVelocity/PluginSpigot might fail to load

    private static final InitAccess inst = new InitAccess();

    public static InitAccess getInstance() {
        var caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        if (!PERMITTED.contains(caller))
            throw new SecurityException();
        return inst;
    }

    private Object initializer, injector;

    private boolean init;

    public void install(ClassLoader cl, Module publish) throws ReflectiveOperationException {
        ensureInit();

        injector.getClass().getMethod("installLoaders", ClassLoader.class)
                .invoke(injector, cl);

        if (publish != null)
            injector.getClass().getMethod("addReadsTo", Module.class)
                    .invoke(injector, publish);
    }

    public ClassLoader provider() {
        try {
            return (ClassLoader) initializer.getClass()
                    .getMethod("getClassProvider")
                    .invoke(initializer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new InternalError(e);
        }
    }

    public ModuleLayer layer() {
        try {
            return (ModuleLayer) initializer.getClass().getMethod("getLayer").invoke(initializer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public void ensureInit() {
        try {
            init();
        } catch (URISyntaxException | ReflectiveOperationException e) {
            Throwable cause = e instanceof InvocationTargetException ite ? ite.getCause() : e;
            throw new RuntimeException("Failure while initializing ulib", cause);
        }
    }

    @SneakyThrows
    public Object construct(String mod, String cn, Object... initArgs) {
        return initializer.getClass()
                .getMethod("construct", String.class, String.class, Object[].class)
                .invoke(initializer, mod, cn, initArgs);
    }

    @Synchronized
    private void init() throws URISyntaxException, ReflectiveOperationException {
        if (init)
            return;
        init = true;
        final var env = getEnv();

        if (getClass().getModule().isNamed()) {
            // loader already loaded as module
            var initializer = Initializer.provide(env.ordinal());
            this.initializer = initializer;
            this.injector = initializer.getInjector();
            return;
        }

        // load self as module
        File me = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());

        var provider = new ModuleClassProvider(null, List.of(me),
                getClass().getClassLoader(), ModuleLayer.boot(), true);

        var loader = provider.getLayer().findLoader("ulib.loader");

        this.initializer = Class.forName("eu.software4you.ulib.loader.impl.init.Initializer", true, loader)
                .getMethod("provide", int.class)
                .invoke(null, env.ordinal());

        this.injector = initializer.getClass().getMethod("getInjector").invoke(this.initializer);
    }

    private EnvironmentProvider.Environment getEnv() {
        return Optional.ofNullable(System.getProperty("ulib.install.env_overwrite"))
                .map(envName -> {
                    try {
                        var env = EnvironmentProvider.Environment.valueOf(envName);
                        System.err.println("(ulib) Overwriting environment with " + env.name());
                        return env;
                    } catch (IllegalArgumentException e) {
                        // ignored
                    }
                    return null;
                })
                .orElseGet(EnvironmentProvider::get);
    }

}
