package eu.software4you.ulib.loader.impl.init;

import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.*;

// handles injection of ulib
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class Injector {

    private final Initializer initializer;

    private final Set<ClassLoader> published = new HashSet<>();
    private final Set<Class<? extends ClassLoader>> injected = new HashSet<>();
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

    private boolean testLoadingRequest(Class<?> requester, String request) {
        var requestingLayer = requester.getModule().getLayer();

        // do not delegate inner requests
        if (requestingLayer != null && requestingLayer == initializer.getClassProvider().getLayer()) {
            return false;
        }

        var pfx = "eu.software4you.ulib.";

        // dont handle other requests
        if (!request.startsWith(pfx))
            return false;
        request = request.substring(pfx.length());

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

    @SneakyThrows
    public void addReadsTo(Module module) {
        var addRecord = initializer.getApiModules().stream()
                .filter(api -> !module.canRead(api))
                .toList();
        if (addRecord.isEmpty())
            return;

        // ReflectUtil.icall(module, "implAddReads()", Param.fromMultiple(module));

        var clParam = Class.forName("eu.software4you.ulib.core.reflect.Param", true, initializer.getClassProvider());
        var clRU = Class.forName("eu.software4you.ulib.core.reflect.ReflectUtil", true, initializer.getClassProvider());

        var params = clParam.getMethod("fromMultiple", Object[].class)
                .invoke(null, new Object[]{new Object[]{module}});

        var method = clRU.getMethod("call", Object.class, String.class, List[].class);

        for (Module m : addRecord) {
            method.invoke(null, m, "implAddReads()", new List[]{(List<?>) params});
        }
    }
}
