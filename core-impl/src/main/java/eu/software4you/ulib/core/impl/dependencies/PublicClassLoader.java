package eu.software4you.ulib.core.impl.dependencies;

import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.security.CodeSource;
import java.security.PermissionCollection;
import java.util.jar.Manifest;

public class PublicClassLoader extends URLClassLoader {

    static {
        ClassLoader.registerAsParallelCapable();
    }

    public PublicClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public PublicClassLoader(URL[] urls) {
        super(urls);
    }

    public PublicClassLoader(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(urls, parent, factory);
    }

    public PublicClassLoader(String name, URL[] urls, ClassLoader parent) {
        super(name, urls, parent);
    }

    public PublicClassLoader(String name, URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory) {
        super(name, urls, parent, factory);
    }

    @Override
    public Class<?> findClass(String moduleName, String name) {
        return super.findClass(moduleName, name);
    }

    @Override
    public Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        return super.loadClass(name, resolve);
    }


    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    @Override
    public URL[] getURLs() {
        return super.getURLs();
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    @Override
    public Package definePackage(String name, Manifest man, URL url) {
        return super.definePackage(name, man, url);
    }

    @Override
    public PermissionCollection getPermissions(CodeSource codesource) {
        return super.getPermissions(codesource);
    }
}
