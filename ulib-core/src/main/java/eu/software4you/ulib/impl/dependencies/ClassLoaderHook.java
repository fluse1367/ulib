package eu.software4you.ulib.impl.dependencies;

import eu.software4you.common.collection.Pair;
import eu.software4you.transform.Callback;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.jar.JarFile;

// hook for dependency injection (literally)
public final class ClassLoaderHook {

    // The register contains the acceptable classes and the respective class loader.
    private final Map<ClassLoader, Pair<Collection<String>, ExposedClassLoader>> register = new HashMap<>();

    // registers a file to a classloader
    // inits an ecl instance if necessary
    @SneakyThrows
    void register(ClassLoader cl, File file) {
        ULib.logger().finer(() -> String.format("Registering %s with %s", file, cl));

        if (!register.containsKey(cl)) {
            // not known yet, init
            val classes = collectClasses(file);
            val loader = new ExposedClassLoader(new URL[]{file.toURI().toURL()});

            register.put(cl, new Pair<>(classes, loader));
            return;
        }

        // already known, only add file and classes
        val pair = register.get(cl);
        pair.getFirst().addAll(collectClasses(file));
        pair.getSecond().addFile(file);
    }

    // un-registers the classloader
    void purge(ClassLoader cl) {
        register.remove(cl);
    }

    // collects all classes within a file
    @SneakyThrows
    List<String> collectClasses(File file) {
        JarFile jar = new JarFile(file);

        List<String> classes = new ArrayList<>(jar.size());

        val it = jar.entries();
        while (it.hasMoreElements()) {
            val entry = it.nextElement();

            String path = entry.getName();
            if (entry.isDirectory() || !path.endsWith(".class"))
                continue;

            String clName = path.replace("/", ".").substring(0, path.length() - 6);
            classes.add(clName);
        }

        return classes;
    }

    // shortcut to receive the ecl instance
    private ExposedClassLoader loader(Object clInstance, String clName) {
        if (!(clInstance instanceof ClassLoader) || !register.containsKey(clInstance))
            return null;

        val pair = register.get(clInstance);
        val register = pair.getFirst();

        if (!register.contains(clName))
            return null;

        return pair.getSecond();
    }

    /* actual hooks */

    // hooks into the findClass method of a classloader
    public void findClass(String name, Callback<Class<?>> cb) throws ClassNotFoundException {
        val loader = loader(cb.self(), name);
        if (loader != null)
            cb.setReturnValue(loader.findClass(name));
    }

    // hooks into the loadClass method of a classloader
    public void loadClass(String name, boolean resolve, Callback<Class<?>> cb) throws ClassNotFoundException {
        val loader = loader(cb.self(), name);
        if (loader != null)
            cb.setReturnValue(loader.loadClass(name, resolve));
    }
}
