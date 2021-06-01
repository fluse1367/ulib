package eu.software4you.ulib.impl.dependencies;

import eu.software4you.dependencies.DependencyLoader;
import eu.software4you.reflect.Parameter;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;
import java.util.logging.Level;

@Impl(DependencyLoader.class)
final class DependencyLoaderImpl extends DependencyLoader {
    @Await
    private static Agent agent;

    private final ClassLoaderHook hook = new ClassLoaderHook();
    private final Injector injector = new Injector(hook);

    private DependencyLoaderImpl() {
    }

    private boolean tryUrlLoad(File file, ClassLoader cl, boolean fallback, boolean fallbackWarn) {
        if (!(cl instanceof URLClassLoader)) {
            return false;
        }

        try {
            ULib.logger().finer(() -> String.format("Loading %s as URL with %s", file, cl.getClass().getName()));
            load0(file.toURI().toURL(), (URLClassLoader) cl);
        } catch (Throwable thr) {
            if (fallback && Agent.available()) { // agent fallback
                sysFallback(file, cl, fallbackWarn);
            } else {
                ULib.logger().log(Level.SEVERE, thr, () ->
                        String.format("Could not load %s with %s", file, cl.getClass().getName()));
                return false;
            }
        }

        return true;
    }

    private void sysFallback(File file, ClassLoader cl, boolean warn) {
        if (warn)
            ULib.logger().warning(String.format(
                    "Loading %s with system class loader instead of %s (fallback)", file, cl.getClass().getName()));
        sysLoad0(file);
    }

    @Override
    protected void free0(ClassLoader cl) {
        injector.purge(cl);
    }

    @SneakyThrows
    @Override
    protected void sysLoad0(File file) {
        Agent.verifyAvailable();
        ULib.logger().finest(() -> String.format("Loading %s with the system class loader", file));
        agent.appendJar(new JarFile(file));
    }

    @Override
    protected void load0(File file, ClassLoader cl, boolean fallback, boolean exclusive) {
        if (cl == ClassLoader.getSystemClassLoader() && !tryUrlLoad(file, cl, true, false)) {
            sysLoad0(file);
            return;
        }

        if (injector.acceptable(cl)) {

            // try addURL() first if not exclusive
            if (!exclusive && tryUrlLoad(file, cl, fallback, true))
                return;

            // hook into cl
            injector.into(cl.getClass());

            // register file to CL
            hook.register(cl, file);
            return;
        }

        if (fallback && !exclusive && Agent.available()) {
            sysFallback(file, cl, true);
        }
    }

    @Override
    protected void load0(URL url) {
        val cl = ReflectUtil.getCallerClass(2).getClassLoader();
        if (cl instanceof URLClassLoader)
            load0(url, (URLClassLoader) cl);
        throw new UnsupportedOperationException(String.format("%s is not assignable as %s",
                cl.getClass().getName(), URLClassLoader.class.getName()));
    }

    @SneakyThrows
    @Override
    protected void load0(URL url, URLClassLoader classLoader) {
        ReflectUtil.forceCall(URLClassLoader.class, classLoader, "addURL()",
                Parameter.single(URL.class, url));
    }
}
