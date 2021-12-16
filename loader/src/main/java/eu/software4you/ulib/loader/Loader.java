package eu.software4you.ulib.loader;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

public class Loader extends URLClassLoader {


    @SneakyThrows
    private static URL toUrl(File f) {
        return f.toURI().toURL();
    }

    static LayeredLoaderContainer withModules(Collection<File> files, Collection<Path> modulePath, Predicate<String> moduleNameFilter, ClassLoader parentLoader) {
        var loader = new Loader(files, parentLoader);

        var parent = Optional.ofNullable(Loader.class.getModule().getLayer())
                .orElseGet(ModuleLayer::boot);


        var finder = findModules(modulePath, moduleNameFilter);
        Collection<String> modules = finder.findAll().stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .toList();

        System.out.println(Arrays.toString(modules.toArray()));
        var conf = parent.configuration().resolve(finder, ModuleFinder.of(), modules);
        var layer = parent.defineModulesWithOneLoader(conf, loader);
        return new LayeredLoaderContainer(loader, layer);
    }

    private static ModuleFinder findModules(Collection<Path> modulePath, Predicate<String> filter) {
        var paths = new ArrayList<>(modulePath);

        var finder = ModuleFinder.of(paths.toArray(Path[]::new));

        Optional<ModuleReference> oref;
        while ((oref = finder.findAll().stream()
                .filter(mref -> !filter.test(mref.descriptor().name()))
                .findAny()).isPresent()) {

            var ref = oref.get();
            paths.removeIf(p -> p.toUri().equals(ref.location().orElseThrow()));
            finder = ModuleFinder.of(paths.toArray(Path[]::new));
        }

        return finder;
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    static class LayeredLoaderContainer {
        public final Loader loader;
        public final ModuleLayer layer;
    }

    Loader(Collection<File> files, ClassLoader parent) {
        super(files.stream().map(Loader::toUrl).toArray(URL[]::new), parent);
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
