package eu.software4you.ulib.loader.impl.init;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import eu.software4you.ulib.loader.impl.EnvironmentProvider;
import eu.software4you.ulib.loader.impl.dependency.DependencyProvider;
import eu.software4you.ulib.loader.impl.dependency.DependencyTransformer;
import eu.software4you.ulib.loader.impl.install.ModuleClassProvider;
import lombok.*;

import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static eu.software4you.ulib.loader.impl.EnvironmentProvider.Environment.STANDALONE;
import static eu.software4you.ulib.loader.impl.EnvironmentProvider.Environment.VELOCITY;

// initializes ulib
public class Initializer {

    private static Initializer instance;

    @Synchronized
    public static Initializer provide() {
        if (instance == null) {
            instance = new Initializer();
        }
        return instance;
    }

    @Getter
    private final Injector injector;
    private final DependencyProvider dependencyProvider = new DependencyProvider();

    private Collection<File> filesModules, filesAdditional;

    @Getter(AccessLevel.PACKAGE)
    private ModuleClassProvider classProvider;
    @Getter(AccessLevel.PACKAGE)
    private Set<Module> apiModules;
    @Getter
    private ModuleLayer layer;
    private final ClassLoader coreLoader;

    private Initializer() {
        this.injector = new Injector(this);

        provideDependencies();
        initLoaders();
        this.coreLoader = classProvider.getLayer().findLoader("ulib.core");
        initImpl();
        injector.initDelegation();
    }

    @SneakyThrows
    Class<?> coreClass(String name) {
        return Class.forName(name, true, coreLoader);
    }

    private void provideDependencies() {
        List<String> modules = new ArrayList<>(1);
        modules.add("core");
        var env = EnvironmentProvider.get();
        if (env != STANDALONE) {
            modules.add("minecraft");
            modules.add(env.name().toLowerCase());
        }
        this.filesModules = dependencyProvider.extractModules(modules);

        var transformer = new DependencyTransformer();

        Predicate<String> filter = coords ->
                !(env == VELOCITY && coords.startsWith("org.slf4j:"));
        this.filesAdditional = dependencyProvider.downloadLibraries(modules, filter, transformer::transform);
    }

    private void initLoaders() {
        // init loader for ulib regular layer
        var files = Stream.of(filesModules.stream(), filesAdditional.stream())
                .flatMap(s -> s)
                .toList();

        var directParent = Optional.ofNullable(getClass().getModule().getLayer()).orElseGet(ModuleLayer::boot);

        List<ModuleLayer> parentLayers = new ArrayList<>(1);

        boolean comply = false;
        switch (System.getProperty("ulib.install.module_layer", "default")) {
            case "boot":
                System.err.println("[ulib-loader] Ignoring parent module layer");
                break;
            case "comply":
                System.err.println("[ulib-loader] Respecting parent modules");
                comply = true;
                // fallthrough
            case "default":
                // fallthrough
            default:
                parentLayers.add(directParent);
        }

        this.classProvider = new ModuleClassProvider(null, files, getClass().getClassLoader(), parentLayers, comply);


        this.apiModules = (this.layer = this.classProvider.getLayer()).modules().stream()
                .filter(m -> m.getName().startsWith("ulib."))
                .collect(Collectors.toSet());

        // add reads record if necessary
        var me = getClass().getModule();
        apiModules.stream()
                .filter(m -> !me.canRead(m))
                .forEach(me::addReads);
    }

    @SneakyThrows
    private void initImpl() {
        // agent init
        if (!System.getProperties().containsKey("ulib.javaagent") && !new AgentInstaller().install()) {
            throw new RuntimeException("Unable to install agent");
        }

        // pass agent to implementation
        var clInit = coreClass("eu.software4you.ulib.core.impl.init.Init");
        clInit.getMethod("init", Object.class).invoke(null, System.getProperties().remove("ulib.javaagent"));
    }

}
