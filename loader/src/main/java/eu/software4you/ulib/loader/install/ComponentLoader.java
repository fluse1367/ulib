package eu.software4you.ulib.loader.install;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

public final class ComponentLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    @SneakyThrows
    private static URL toUrl(File f) {
        return f.toURI().toURL();
    }

    ComponentLoader(Collection<File> files, java.lang.ClassLoader parent) {
        super(files.stream().map(ComponentLoader::toUrl).toArray(URL[]::new), parent);
    }

    @Override
    public void addURL(URL url) { // <- make ulib able to add other jars later on
        super.addURL(url);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }
}
