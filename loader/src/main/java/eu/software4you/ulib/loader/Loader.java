package eu.software4you.ulib.loader;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import lombok.SneakyThrows;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;

public class Loader extends URLClassLoader {

    static {
        load();
    }

    private Loader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    public static void $() {
        // empty
    }

    @SneakyThrows
    private static void load() {
        // extraction
        var extractor = new Extractor();
        var files = extractor.extract();
        var superFiles = extractor.extractSuper();
        var urls = Arrays.stream(files)
                .map(Loader::toUrl)
                .toArray(URL[]::new);

        // agent init
        if (!System.getProperties().containsKey("ulib.javaagent")) {
            new AgentInstaller().install();
        }
        var appendToBootstrapClassLoaderSearch = System.getProperties().remove("ulib.loader.javaagent");

        // ulib init
        var parent = Loader.class.getClassLoader();
        var loader = new Loader(urls, parent);
        var classULib = loader.loadClass("eu.software4you.ulib.core.ULib");

        // append super files to system classpath
        var methodSysLoad = loader.loadClass("eu.software4you.ulib.core.api.dependencies.DependencyLoader")
                .getMethod("sysLoad", File.class);
        for (File file : superFiles) {
            methodSysLoad.invoke(null, file);
            ((Consumer<JarFile>) appendToBootstrapClassLoaderSearch).accept(new JarFile(file));
        }

        // prevent circularity error by loading ReflectUtil beforehand
        {
            Class<?> classReflectUtil = loader.loadClass("eu.software4you.ulib.core.api.reflect.ReflectUtil");
            classULib.getMethod("service", Class.class).invoke(null, classReflectUtil);
            classReflectUtil.getMethod("getCallerClass", int.class).invoke(null, 0);
        }

        // publish ulib API to current class loader
        var cl = loader.loadClass("eu.software4you.ulib.core.impl.dependencies.DelegationInjector");
        var methodDelegate = cl.getMethod("delegate", ClassLoader.class, ClassLoader.class, Predicate.class);

        Predicate<String> filter = name -> name.startsWith("eu.software4you.ulib.core.api.") || name.equals("eu.software4you.ulib.core.ULib");
        methodDelegate.invoke(null, /*target*/parent, /*delegate*/loader, filter);
    }

    @SneakyThrows
    private static URL toUrl(File f) {
        return f.toURI().toURL();
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
