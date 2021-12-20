package eu.software4you.ulib.loader.install;

import eu.software4you.ulib.loader.agent.AgentInstaller;
import eu.software4you.ulib.loader.install.provider.DependencyProvider;
import eu.software4you.ulib.loader.install.provider.DependencyTransformer;
import eu.software4you.ulib.loader.install.provider.EnvironmentProvider;
import eu.software4you.ulib.loader.install.provider.ModuleClassProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.Synchronized;

import java.io.File;
import java.util.*;
import java.util.function.*;
import java.util.jar.JarFile;
import java.util.stream.Stream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Installer {

    private static final Installer instance = new Installer();
    private final Set<ClassLoader> published = new HashSet<>();
    private final Set<Class<? extends ClassLoader>> injected = new HashSet<>();
    private final DependencyProvider dependencyProvider = new DependencyProvider();

    private boolean init;

    private Collection<File> filesLibrary, filesModule, filesSuper, filesAdditional;
    private ModuleClassProvider classProviderSuper, classProvider;
    private Module moduleCoreApi;

    private Object delegationInjector;

    @SneakyThrows
    private void init() {
        if (init)
            return;
        init = true;

        provideDependencies();
        initAgent();
        initLoaders();
        loadULib();
        initInjector();
    }

    private void provideDependencies() {
        this.filesLibrary = dependencyProvider.extractLibrary();
        this.filesModule = dependencyProvider.extractModule();
        this.filesSuper = dependencyProvider.extractSuper();
        var transformer = new DependencyTransformer();
        this.filesAdditional = dependencyProvider.downloadAdditional(transformer::transform);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private void initAgent() {
        // agent init
        if (!System.getProperties().containsKey("ulib.javaagent") && !new AgentInstaller().install()) {
            throw new RuntimeException("Unable to install agent");
        }

        // append super files to system classpath
        var consumer = System.getProperties().remove("ulib.loader.javaagent");
        try {
            var con = (Consumer<JarFile>) consumer;
            for (File file : filesSuper) {
                con.accept(new JarFile(file));
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Javaagent provided property is invalid");
        }
    }

    private void initLoaders() {
        // init loader for super files
        classProviderSuper = new ModuleClassProvider(null, filesSuper, ClassLoader.getSystemClassLoader(), ModuleLayer.boot());
        var layerSuper = classProviderSuper.getLayer();

        // init loader for ulib regular layer
        var files = Stream.of(filesLibrary.stream(), filesModule.stream(), filesAdditional.stream())
                .flatMap(s -> s)
                .toList();

        var directParent = Optional.ofNullable(getClass().getModule().getLayer()).orElseGet(ModuleLayer::boot);

        List<ModuleLayer> parentLayers = new ArrayList<>(2);
        parentLayers.add(layerSuper);

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
        this.moduleCoreApi = this.classProvider.getLayer().findModule("ulib.core.api")
                .orElseThrow(IllegalStateException::new);
    }

    @SneakyThrows
    private void loadULib() {
        System.getProperties().put("ulib.environment", EnvironmentProvider.get().ordinal());
        Class.forName("eu.software4you.ulib.core.ULib", true, moduleCoreApi.getClassLoader());
    }

    @SneakyThrows
    private void initInjector() {
        BiPredicate<Class<?>, String> checkLoadingRequest = (requester, request) -> {
            var requestingLayer = requester.getModule().getLayer();

            if (request.startsWith("eu.software4you.ulib.supermodule.")) {
                return requestingLayer != classProviderSuper.getLayer();
            }

            // do not delegate other inner requests
            if (requestingLayer != null && requestingLayer == classProvider.getLayer()) {
                return false;
            }

            // only access to the core API
            return request.startsWith("eu.software4you.ulib.core.api.") || request.equals("eu.software4you.ulib.core.ULib");
        };
        this.delegationInjector = constructInjector(classProvider, published::contains, checkLoadingRequest);
    }

    @SneakyThrows
    private Object constructInjector(ModuleClassProvider provider,
                                     Predicate<ClassLoader> checkClassLoader,
                                     BiPredicate<Class<?>, String> checkLoadingRequest) {

        BiFunction<String, Boolean, Class<?>> delegateLoadClass = provider::loadClass;
        Function<String, Class<?>> delegateFindClass = provider::findClass;
        BiFunction<String, String, Class<?>> delegateFindModuleClass = provider::findClass;

        // construct hook instance
        var loaderCoreImpl = classProvider.getLayer().findLoader("ulib.core");
        var classHook = Class.forName("eu.software4you.ulib.core.impl.delegation.DelegationHook", true, loaderCoreImpl);
        var constructorHook = classHook.getConstructor(BiFunction.class, Function.class, BiFunction.class, Predicate.class, BiPredicate.class);
        var hook = constructorHook.newInstance(delegateLoadClass, delegateFindClass, delegateFindModuleClass, checkClassLoader, checkLoadingRequest);

        // construct injector instance
        var classInjector = Class.forName("eu.software4you.ulib.core.impl.delegation.DelegationInjector", true, loaderCoreImpl);
        var constructorInjector = classInjector.getConstructor(classHook);
        return constructorInjector.newInstance(hook);
    }

    @SneakyThrows
    private void inject(Object injector, ClassLoader target) {
        var cl = target.getClass();
        var methodInject = delegationInjector.getClass().getMethod("inject", Class.class);
        methodInject.invoke(injector, cl);
    }

    private void installLoaders(ClassLoader target) {
        if (published.contains(target))
            throw new IllegalArgumentException("The API has already been installed to " + target);
        published.add(target);

        var cl = target.getClass();
        if (injected.contains(cl))
            return;

        inject(delegationInjector, target);
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
     * Returns the module object from the uLib API.
     *
     * @return the module object
     */
    public static Module getModule() {
        return instance.moduleCoreApi;
    }
}
