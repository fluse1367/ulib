package eu.software4you.ulib.loader;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import lombok.SneakyThrows;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class Init {

    static {
        new Init().doInit();
    }

    private final ClassLoader loaderParent;
    private final Extractor extractor;

    private File[] filesLibrary, filesModule, filesSuper;
    private Loader loader;
    private ModuleLayer layer;
    private Class<?> classULib;

    private Init() {
        this.loaderParent = getClass().getClassLoader();
        this.extractor = new Extractor();
    }

    private void doInit() {
        extract();
        initClassLoader();
        load();
        publish();
    }

    private void extract() {
        this.filesLibrary = extractor.extractLibrary();
        this.filesModule = extractor.extractModule();
        this.filesSuper = extractor.extractSuper();
    }

    private void initClassLoader() {
        var files = Stream.concat(Stream.of(filesLibrary), Stream.of(filesModule)).toList();
        var paths = Stream.of(
                        Stream.of(filesLibrary),
                        Stream.of(filesModule),
                        Stream.of(filesSuper)
                ).flatMap(s -> s.map(File::toPath))
                .toList();

        Collection<String> blackList = Arrays.asList(
                "maven.repository.metadata",
                "maven.model.builder"
        );

        var cont = Loader.withModules(files, paths,
                name -> !blackList.contains(name), loaderParent);

        this.loader = cont.loader;
        this.layer = cont.layer;
    }

    @SneakyThrows
    private void load() {
        // agent init
        if (!System.getProperties().containsKey("ulib.javaagent")) {
            new AgentInstaller().install();
        }
        var appendToBootstrapClassLoaderSearch = System.getProperties().remove("ulib.loader.javaagent");

        // ulib init
        var mLoader = layer.findLoader("ulib.core.api");
        classULib = Class.forName("eu.software4you.ulib.core.ULib", true, mLoader);
        System.out.println("ULib main class loaded: " + classULib + " module " + classULib.getModule());

        // append super files to system classpath
        var methodSysLoad = loader.loadClass("eu.software4you.ulib.core.api.dependencies.DependencyLoader")
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
            Class<?> classReflectUtil = loader.loadClass("eu.software4you.ulib.core.api.reflect.ReflectUtil");
            classULib.getMethod("service", Class.class).invoke(null, classReflectUtil);
            classReflectUtil.getMethod("getCallerClass", int.class).invoke(null, 0);
        }

        // publish ulib API to current class loader
        var classInjector = loader.loadClass("eu.software4you.ulib.core.impl.dependencies.DelegationInjector");
        var methodDelegate = classInjector.getMethod("delegate", ClassLoader.class, ClassLoader.class, Predicate.class);

        Predicate<String> filter = name -> name.startsWith("eu.software4you.ulib.core.api.") || name.equals("eu.software4you.ulib.core.ULib");
        methodDelegate.invoke(null, /*target*/loaderParent, /*delegate*/loader, filter);
    }


}
