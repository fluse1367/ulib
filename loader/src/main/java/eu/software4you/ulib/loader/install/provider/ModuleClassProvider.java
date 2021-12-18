package eu.software4you.ulib.loader.install.provider;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ModuleClassProvider {

    private final ModuleClassProvider providerParent;
    @Getter
    private final ModuleLayer.Controller controller;
    @Getter
    private final ModuleLayer layer;

    public ModuleClassProvider(ModuleClassProvider providerParent, Collection<File> files, ClassLoader loaderParent, ModuleLayer parentLayer) {
        this(providerParent, files, loaderParent, parentLayer, false);
    }

    public ModuleClassProvider(ModuleClassProvider providerParent, Collection<File> files, ClassLoader loaderParent, ModuleLayer parentLayer, boolean comply) {
        this(providerParent, files, loaderParent, List.of(parentLayer), comply);
    }

    public ModuleClassProvider(ModuleClassProvider providerParent, Collection<File> files, ClassLoader loaderParent, List<ModuleLayer> parentLayers, boolean comply) {
        this.providerParent = providerParent;

        var finder = makeFinder(files, parentLayers, comply);
        var emptyFinder = ModuleFinder.of();
        var roots = finder.findAll().stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .toList();


        var parents = parentLayers.stream().map(ModuleLayer::configuration).toList();

        var conf = Configuration.resolve(finder, parents, emptyFinder, roots);

        this.controller = ModuleLayer.defineModulesWithOneLoader(conf, parentLayers, loaderParent);
        this.layer = controller.layer();
    }

    private ModuleFinder makeFinder(Collection<File> files, List<ModuleLayer> parentLayers, boolean comply) {
        List<Path> paths = new ArrayList<>(files.stream().map(File::toPath).toList());

        ModuleFinder finder;
        do {
            finder = ModuleFinder.of(paths.toArray(Path[]::new));
        } while (comply && (paths = checkComply(finder, parentLayers)) != null);

        return finder;
    }

    private List<Path> checkComply(ModuleFinder finder, List<ModuleLayer> parentLayers) {
        AtomicBoolean foundInvalid = new AtomicBoolean(false);
        List<Path> leftOver = new LinkedList<>();

        finder.findAll().forEach(ref -> {
            boolean invalid = parentLayers.stream().anyMatch(layer -> layer.findModule(ref.descriptor().name()).isPresent());
            if (!invalid) {
                leftOver.add(Path.of(ref.location().orElseThrow()));
                return;
            }
            foundInvalid.set(true);
        });

        return foundInvalid.get() ? leftOver : null;
    }

    private ClassLoader findLoaderFor(String name) {
        var i = name.lastIndexOf(".");
        String packageName = name.substring(0, i);
        String className = name.substring(i + 1);

        var om = layer.modules().stream()
                .filter(module -> module.getPackages().contains(packageName))
                .reduce((m1, m2) -> {
                    throw new IllegalStateException("Found more than one module with the same package");
                });
        return om.orElseThrow().getClassLoader();
    }

    @SneakyThrows
    public Class<?> loadClass(String name, boolean resolve) {
        try {
            return Class.forName(name, resolve, findLoaderFor(name));
        } catch (NoSuchElementException e) {
            if (providerParent != null)
                return providerParent.loadClass(name, resolve);
        }
        return null;
    }

    @SneakyThrows
    public Class<?> findClass(String name) {
        try {
            return findLoaderFor(name).loadClass(name);
        } catch (NoSuchElementException e) {
            if (providerParent != null)
                return providerParent.findClass(name);
        }
        return null;
    }

    @SneakyThrows
    public Class<?> findClass(String module, String name) {
        try {
            return layer.findLoader(module).loadClass(name);
        } catch (NoSuchElementException e) {
            if (providerParent != null)
                return providerParent.findClass(module, name);
        }
        return null;
    }
}
