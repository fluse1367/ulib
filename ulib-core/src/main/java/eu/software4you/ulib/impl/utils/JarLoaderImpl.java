package eu.software4you.ulib.impl.utils;

import eu.software4you.reflect.Parameter;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.Agent;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.inject.Impl;
import eu.software4you.utils.JarLoader;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.jar.JarFile;

@Impl(JarLoader.class)
final class JarLoaderImpl extends JarLoader {
    @Await
    private static Agent agent;
    private URLClassLoader urlClassLoader = null;

    private JarLoaderImpl() {
    }

    private URLClassLoader getUrlClassLoader() {
        if (urlClassLoader != null)
            return urlClassLoader;

        ClassLoader cl = JarLoader.class.getClassLoader();
        if (cl instanceof URLClassLoader || (cl = ClassLoader.getSystemClassLoader()) instanceof URLClassLoader) {
            urlClassLoader = (URLClassLoader) cl;
        }
        return urlClassLoader;
    }

    private boolean jre9LoaderAvailable() {
        URLClassLoader ucl = getUrlClassLoader();
        if (ucl != null)
            return false;
        // we're in java 9
        Agent.verifyAvailable();
        return true;
    }

    private URLClassLoader getUrlClassLoaderSafe() {
        URLClassLoader ucl = getUrlClassLoader();
        if (ucl == null) {
            throw new UnsupportedOperationException("Cannot attach jar files to the jvm.");
        }
        return ucl;
    }

    @SneakyThrows
    @Override
    protected void load0(File file) {
        if (jre9LoaderAvailable()) {
            agent.appendJar(new JarFile(file));
            return;
        }

        load(file.toURI().toURL());
    }

    @Override
    protected void load0(URL url) {
        load(url, getUrlClassLoaderSafe());
    }

    @SneakyThrows
    @Override
    protected void load0(URL url, URLClassLoader classLoader) {
        ReflectUtil.forceCall(URLClassLoader.class, classLoader, "addURL()",
                Parameter.single(URL.class, url));
    }
}
