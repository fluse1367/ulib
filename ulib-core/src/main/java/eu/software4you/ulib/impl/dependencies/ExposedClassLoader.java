package eu.software4you.ulib.impl.dependencies;

import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

// URLClassLoader with widened access
final class ExposedClassLoader extends URLClassLoader {
    static {
        ClassLoader.registerAsParallelCapable();
    }

    public ExposedClassLoader(URL[] urls) {
        super(urls);
    }

    public ExposedClassLoader() {
        this(new URL[0]);
    }

    @Override
    protected void addURL(URL url) {
        super.addURL(url);
    }

    @SneakyThrows
    protected void addFile(File file) {
        addURL(file.toURI().toURL());
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }
}
