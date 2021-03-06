package eu.software4you.ulib.loader.impl.init;

import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.*;

import static eu.software4you.ulib.loader.impl.init.Shared.BASE_PACKAGE_PFX;

// handles injection of ulib
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Injector {

    private final Initializer initializer;

    private final Set<ClassLoader> published = new HashSet<>(); // contains all CL's that ulib is installed to
    private final Set<Class<? extends ClassLoader>> injected = new HashSet<>();
    private final Set<ClassLoader> privileged = new HashSet<>(); // contains all CL's that may request loading of ulib classes regardless of installation state
    private Object delegation;

    @SneakyThrows
    void initDelegation() {
        this.delegation = initializer.coreClass("eu.software4you.ulib.core.inject.ClassLoaderDelegation")
                .getConstructor(ClassLoader.class)
                .newInstance(initializer.getClassProvider());
    }

    @SneakyThrows
    void additionally(String method, Class<?>[] paramTypes, Function<? super Object[], Optional<String>> classNameResolver) {
        this.delegation.getClass()
                // additionally(String method, Class<?>[] paramTypes, Function<? super Object[], Optional<String>> classNameResolver)
                .getMethod("additionally", String.class, Class[].class, Function.class)
                .invoke(this.delegation, method, paramTypes, classNameResolver);
    }

    public void privileged(ClassLoader loader, boolean is) {
        if (is) {
            privileged.add(loader);
        } else {
            privileged.remove(loader);
        }
    }

    private boolean testLoadingRequest(Class<?> requester, String request) {
        var requestingLayer = requester.getModule().getLayer();

        // do not delegate inner requests
        if (requestingLayer != null && requestingLayer == initializer.getClassProvider().getLayer()) {
            return false;
        }

        // dont handle other requests
        if (!request.startsWith(BASE_PACKAGE_PFX))
            return false;
        request = request.substring(BASE_PACKAGE_PFX.length());

        // deny access to implementation
        int i = request.indexOf(".");
        if (request.substring(i + 1).startsWith("impl."))
            return false;

        var requestedModule = request.substring(0, i);
        return initializer.getEnvironment().getModules().contains(requestedModule);
    }

    @SneakyThrows
    public void installLoaders(ClassLoader target) {
        if (published.contains(target))
            return;
        published.add(target);

        var cl = target.getClass();
        if (injected.contains(cl))
            return;

        var clIU = initializer.coreClass("eu.software4you.ulib.core.inject.InjectUtil");
        Object result = clIU.getMethod("injectLoaderDelegation", delegation.getClass(), Predicate.class, BiPredicate.class, Class.class)
                .invoke(null,
                        delegation,
                        (Predicate<ClassLoader>) loader -> published.contains(loader) || privileged.contains(loader),
                        (BiPredicate<Class<?>, String>) this::testLoadingRequest,
                        cl);
        try {
            result.getClass().getMethod("rethrow").invoke(result);
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }

        injected.add(cl);
    }

    @SneakyThrows
    public void addReadsTo(Module module) {
        initializer.coreClass("eu.software4you.ulib.core.impl.Internal")
                .getMethod("makeAccessibleTo", Module.class)
                .invoke(null, module);
    }
}
