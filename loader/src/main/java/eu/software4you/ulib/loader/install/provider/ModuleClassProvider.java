package eu.software4you.ulib.loader.install.provider;

import lombok.Getter;
import lombok.SneakyThrows;

import java.io.File;
import java.lang.module.Configuration;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

public final class ModuleClassProvider {

    private final ModuleClassProvider providerParent;
    @Getter
    private final ModuleLayer.Controller controller;
    @Getter
    private final ModuleLayer layer;

    public ModuleClassProvider(ModuleClassProvider providerParent, Collection<File> files, ClassLoader loaderParent, ModuleLayer layerParent, ModuleLayer... otherParents) {
        this.providerParent = providerParent;

        var finder = ModuleFinder.of(files.stream().map(File::toPath).toArray(Path[]::new));
        var emptyFinder = ModuleFinder.of();
        var roots = finder.findAll().stream()
                .map(ModuleReference::descriptor)
                .map(ModuleDescriptor::name)
                .toList();

        var parentLayers = new ArrayList<ModuleLayer>(otherParents.length + 1);
        parentLayers.add(layerParent);
        parentLayers.addAll(Arrays.asList(otherParents));
        var parents = parentLayers.stream().map(ModuleLayer::configuration).toList();

        var conf = Configuration.resolve(finder, parents, emptyFinder, roots);

        this.controller = ModuleLayer.defineModulesWithOneLoader(conf, parentLayers, loaderParent);
        this.layer = controller.layer();
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
