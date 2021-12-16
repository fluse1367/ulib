package eu.software4you.ulib.loader.install;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public final class Installer {

    static {
        new Installer().install();
    }

    private final ClassLoader loaderParent;
    private final DependencyProvider dependencyProvider;

    private Collection<File> filesLibrary, filesModule, filesSuper, filesAdditional;
    private ComponentLoader loader;
    private Class<?> classULib;

    private Installer() {
        this.loaderParent = getClass().getClassLoader();
        this.dependencyProvider = new DependencyProvider();
    }

    private void install() {
        extract();
        load();
        publish();
    }

    private void extract() {
        this.filesLibrary = dependencyProvider.extractLibrary();
        this.filesModule = dependencyProvider.extractModule();
        this.filesSuper = dependencyProvider.extractSuper();
        this.filesAdditional = dependencyProvider.downloadAdditional();
    }

    @SneakyThrows
    private void load() {
        // agent init
        if (!System.getProperties().containsKey("ulib.javaagent") && !new AgentInstaller().install()) {
            throw new RuntimeException("Unable to install agent");
        }
        var appendToBootstrapClassLoaderSearch = System.getProperties().remove("ulib.loader.javaagent");

        // class loader init
        var files = Stream.of(filesLibrary.stream(), filesModule.stream(), filesAdditional.stream())
                .flatMap(s -> s)
                .toList();
        loader = new ComponentLoader(files, loaderParent);

        // ulib init
        classULib = Class.forName("eu.software4you.ulib.core.ULib", true, loader);

        // append super files to system classpath
        var methodSysLoad = Class.forName("eu.software4you.ulib.core.api.dependencies.DependencyLoader", true, loader)
                .getMethod("sysLoad", File.class);
        for (File file : filesSuper) {
            methodSysLoad.invoke(null, file);
            ((Consumer<JarFile>) appendToBootstrapClassLoaderSearch).accept(new JarFile(file));
        }
    }

    @SneakyThrows
    private void publish() {
        // prevent circularity error by loading ReflectUtil beforehand
        {
            Class<?> classReflectUtil = Class.forName("eu.software4you.ulib.core.api.reflect.ReflectUtil", true, loader);
            classULib.getMethod("service", Class.class).invoke(null, classReflectUtil);
            classReflectUtil.getMethod("getCallerClass", int.class).invoke(null, 0);
        }

        // publish ulib API to current class loader
        var classInjector = Class.forName("eu.software4you.ulib.core.impl.dependencies.DelegationInjector", true, loader);
        var methodInjectDelegation = classInjector.getMethod("injectDelegation", ClassLoader.class, ClassLoader.class, Predicate.class);

        Predicate<String> filter = name -> name.startsWith("eu.software4you.ulib.core.api.") || name.equals("eu.software4you.ulib.core.ULib");
        methodInjectDelegation.invoke(null, /*target*/loaderParent, /*delegate*/loader, filter);
    }


}
