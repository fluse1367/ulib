package eu.software4you.ulib.loader.impl.init;

import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.Util;
import eu.software4you.ulib.loader.impl.install.ModuleClassProvider;
import eu.software4you.ulib.loader.install.Environment;
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
        inst.ensureAccess();
        return inst;
    }

    private Object initializer, injector;
    private EnvironmentProvider.Environment environment;

    private boolean init;

    private void ensureAccess() {
        var caller = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        if (!PERMITTED.contains(caller)
            && caller.getClassLoader() != getClass().getClassLoader())
            throw new SecurityException();
    }

    public void ensureInit() {
        ensureAccess();
        try {
            init();
        } catch (URISyntaxException | ReflectiveOperationException e) {
            Throwable cause = e instanceof InvocationTargetException ite ? ite.getCause() : e;
            throw new RuntimeException("Failure while initializing ulib", cause);
        }
    }

    public void install(ClassLoader cl, Module publish) throws ReflectiveOperationException {
        ensureAccess();
        ensureInit();

        injector.getClass().getMethod("installLoaders", ClassLoader.class)
                .invoke(injector, cl);

        if (publish != null)
            injector.getClass().getMethod("addReadsTo", Module.class)
                    .invoke(injector, publish);
    }

    public void privileged(ClassLoader loader, boolean is) {
        ensureAccess();
        ensureInit();

        try {
            injector.getClass().getMethod("privileged", ClassLoader.class, boolean.class)
                    .invoke(injector, loader, is);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new InternalError(e);
        }
    }

    public ClassLoader provider() {
        ensureAccess();
        ensureInit();
        try {
            return (ClassLoader) initializer.getClass()
                    .getMethod("getClassProvider")
                    .invoke(initializer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new InternalError(e);
        }
    }

    public ModuleLayer layer() {
        ensureAccess();
        ensureInit();

        try {
            return (ModuleLayer) initializer.getClass().getMethod("getLayer").invoke(initializer);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

    public Environment getEnvironment() {
        if (environment == null)
            throw new IllegalStateException("Not initialized");
        return environment.asExposed();
    }

    @SneakyThrows
    public Object construct(String mod, String cn, Object... initArgs) {
        ensureAccess();
        ensureInit();

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
        if (environment != null)
            return environment;

        return environment = Optional.ofNullable(System.getProperty("ulib.install.env_overwrite"))
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
