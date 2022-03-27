package eu.software4you.ulib.loader.install;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import eu.software4you.ulib.loader.install.provider.*;
import lombok.*;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static eu.software4you.ulib.loader.install.provider.EnvironmentProvider.Environment.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Installer {

    private static final Installer instance = new Installer();
    private final Set<ClassLoader> published = new HashSet<>();
    private final Set<Class<? extends ClassLoader>> injected = new HashSet<>();
    private final DependencyProvider dependencyProvider = new DependencyProvider();

    private boolean init;

    private Collection<File> filesModules, filesAdditional;
    private ModuleClassProvider classProvider;
    private Object delegation;
    private Module moduleCore;

    @SneakyThrows
    private void init() {
        if (init)
            return;
        init = true;

        provideDependencies();
        initLoaders();
        initImpl();
        initDelegation();
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
        this.moduleCore = this.classProvider.getLayer().findModule("ulib.core")
                .orElseThrow(IllegalStateException::new);

        // add reads record if necessary
        var me = getClass().getModule();
        if (!me.canRead(moduleCore)) {
            me.addReads(moduleCore);
        }
    }

    @SneakyThrows
    private void initImpl() {
        // agent init
        if (!System.getProperties().containsKey("ulib.javaagent") && !new AgentInstaller().install()) {
            throw new RuntimeException("Unable to install agent");
        }

        // pass agent to implementation
        var clInit = Class.forName("eu.software4you.ulib.core.impl.init.Init", true, moduleCore.getClassLoader());
        clInit.getMethod("init", Object.class).invoke(null, System.getProperties().remove("ulib.javaagent"));
    }

    @SneakyThrows
    private void initDelegation() {
        this.delegation = Class.forName("eu.software4you.ulib.core.inject.ClassLoaderDelegation", true, moduleCore.getClassLoader())
                .getConstructor(ClassLoader.class)
                .newInstance(classProvider);
    }

    private boolean testLoadingRequest(Class<?> requester, String request) {
        var requestingLayer = requester.getModule().getLayer();

        // do not delegate inner requests
        if (requestingLayer != null && requestingLayer == classProvider.getLayer()) {
            return false;
        }

        var env = EnvironmentProvider.get();
        return
                // access to core API
                request.startsWith("eu.software4you.ulib.core.") && !request.startsWith("eu.software4you.ulib.core.impl.") /* no access to impl */
                // access to minecraft api
                || env != STANDALONE && request.startsWith("eu.software4you.ulib.minecraft.api.")
                // access to velocity api
                || env == VELOCITY && request.startsWith("eu.software4you.ulib.velocity.api.")
                // access to spigot api
                || env == SPIGOT && request.startsWith("eu.software4you.ulib.spigot.api.")
                ;
    }

    @SneakyThrows
    private void installLoaders(ClassLoader target) {
        if (published.contains(target))
            throw new IllegalArgumentException("The API has already been installed to " + target);
        published.add(target);

        var cl = target.getClass();
        if (injected.contains(cl))
            return;

        var clIU = Class.forName("eu.software4you.ulib.core.inject.InjectUtil", true, moduleCore.getClassLoader());
        Object result = clIU.getMethod("injectLoaderDelegation", delegation.getClass(), Predicate.class, BiPredicate.class, Class.class)
                .invoke(null,
                        delegation,
                        (Predicate<ClassLoader>) published::contains,
                        (BiPredicate<Class<?>, String>) this::testLoadingRequest,
                        cl);
        try {
            result.getClass().getMethod("rethrow").invoke(result);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        injected.add(cl);
    }

    /**
     * Installs the uLib API to a class loader by injecting code into it.
     *
     * @param target the class loader to install the API to
     * @throws IllegalArgumentException If the uLib API has already been installed to that class loader
     * @implNote If the target is the loader of a named module, an {@link Module#addReads(Module) reads} record must be
     * added by that module manually in order for that module to be able to interact with the uLib API.
     * Its {@link Module module object} can be obtained with {@link #getModule()}.
     */
    @Synchronized
    public static void installTo(ClassLoader target) throws IllegalArgumentException {
        if (!instance.init)
            instance.init();
        instance.installLoaders(target);
    }

    /**
     * Install the uLib API to the class loader of the calling class.
     *
     * @implNote If the caller is part of a named module, an {@link Module#addReads(Module) reads} record must be
     * added by that module manually in order for that module to be able to interact with the uLib API.
     * Its {@link Module module object} can be obtained with {@link #getModule()}.
     */
    @Synchronized
    public static void installMe() {
        if (!instance.init)
            instance.init();

        var loader = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getClassLoader();
        if (instance.published.contains(loader))
            return;
        instance.installLoaders(loader);
    }

    /**
     * Returns the module object from the uLib core.
     *
     * @return the module object
     */
    public static Module getModule() {
        return instance.moduleCore;
    }
}
