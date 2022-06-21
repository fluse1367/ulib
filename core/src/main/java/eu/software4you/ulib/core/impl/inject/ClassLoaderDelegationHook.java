package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Expect;

import java.lang.StackWalker.StackFrame;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.*;
import java.util.stream.Stream;

public final class ClassLoaderDelegationHook {

    // for injection
    private final Class<? extends ClassLoader> targetClazz;
    private final HookInjection injection;

    // marks methods that should be injected additionally to the usual methods
    // (method name) -> (parameter types) -> (mapping function to determine class name to resolve)
    private final Map<String, Map<Class<?>[], Function<Object[], Optional<String>>>> additionalHooks;

    // for hook execution
    private final ClassLoaderDelegation delegation;
    private final Predicate<ClassLoader> filterClassLoader; // check class loader delegated
    private final BiPredicate<Class<?>, String> filterLoadingRequest; // requesting class, requested name

    // keep track of thread requesting class loading
    // method "id" -> (resource identifier -> accessing threads)
    private final Map<Integer, Map<String, Collection<Thread>>> threadAccess = new ConcurrentHashMap<>();

    public ClassLoaderDelegationHook(Class<? extends ClassLoader> targetClazz,
                                     Map<String, Map<Class<?>[], Function<Object[], Optional<String>>>> additional,
                                     ClassLoaderDelegation delegation,
                                     Predicate<ClassLoader> filterClassLoader,
                                     BiPredicate<Class<?>, String> filterLoadingRequest) {
        this.targetClazz = targetClazz;
        this.additionalHooks = additional;
        this.injection = new HookInjection(targetClazz);

        this.delegation = delegation;
        this.filterClassLoader = filterClassLoader;
        this.filterLoadingRequest = filterLoadingRequest;
    }

    public Expect<Void, Exception> inject() {
        // delegate class finding/loading

        var headSpec = InjectUtil.createHookingSpec(HookPoint.HEAD, null, (Integer[]) null);

        ReflectUtil.findUnderlyingMethod(targetClazz, "findClass", true, String.class)
                .ifPresent(into -> injection.<Class<?>>addHook(into, headSpec,
                        (p, c) -> hook_findClass((String) p[0], c)
                ));

        ReflectUtil.findUnderlyingMethod(targetClazz, "findClass", true, String.class, String.class)
                .ifPresent(into -> injection.<Class<?>>addHook(into, headSpec,
                        (p, c) -> hook_findClass((String) p[0], (String) p[1], c)
                ));

        ReflectUtil.findUnderlyingMethod(targetClazz, "loadClass", true, String.class, boolean.class)
                .ifPresent(into -> injection.<Class<?>>addHook(into, headSpec,
                        (p, c) -> hook_loadClass((String) p[0], (boolean) p[1], c)
                ));

        additionalHooks.forEach((name, map) -> map.forEach((types, mapper) -> ReflectUtil
                .findUnderlyingMethod(targetClazz, name, true, types)
                .ifPresent(into -> injection.<Class<?>>addHook(into, headSpec,
                        (p, cb) -> mapper.apply(p).ifPresent(
                                nameToResolve -> this.hook_findClass(nameToResolve, cb)
                        )
                ))
        ));

        // delegate resource finding

        ReflectUtil.findUnderlyingMethod(targetClazz, "findResource", true, String.class)
                .ifPresent(into -> injection.<URL>addHook(into, headSpec,
                        (p, c) -> hook_findResource((String) p[0], c)
                ));

        ReflectUtil.findUnderlyingMethod(targetClazz, "findResource", true, String.class, String.class)
                .ifPresent(into -> injection.<URL>addHook(into, headSpec,
                        (p, c) -> hook_findResource((String) p[0], (String) p[1], c)
                ));

        return injection.inject();
    }

    // access check

    private Class<?> identifyClassLoadingRequestSource() {
        // walk through stack and find first class that is not a class loader anymore
        var frames = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(Stream::toList);

        boolean walkingClassLoaderChain = false;
        for (StackFrame frame : frames) {
            var cl = frame.getDeclaringClass();
            boolean isClassLoader = ClassLoader.class.isAssignableFrom(cl);

            if (isClassLoader && !walkingClassLoaderChain) {
                walkingClassLoaderChain = true;
            } else if (walkingClassLoaderChain && !isClassLoader) {
                // found class that is nod a class loader
                return cl;
            }
        }
        throw new IllegalStateException(); // a class loader cannot be the source of the class loading request
    }

    private boolean checkCl(Object source) {
        return source instanceof ClassLoader cl && filterClassLoader.test(cl);
    }

    private boolean checkClassRequest(Object source, String name) {
        return checkCl(source)
               && filterLoadingRequest.test(identifyClassLoadingRequestSource(), name);
    }

    // recursion protection

    private boolean enter(int id, String resourceIden) {
        var accessMap = threadAccess.computeIfAbsent(id, m -> new ConcurrentHashMap<>());
        var coll = accessMap.computeIfAbsent(resourceIden, rec -> new ConcurrentLinkedQueue<>());

        if (coll.contains(Thread.currentThread()))
            return false; // thread access already in progress

        coll.add(Thread.currentThread());
        return true;
    }

    private void leave(int id, String resourceIden) {
        var accessMap = threadAccess.get(id);
        if (accessMap == null)
            return;

        var coll = accessMap.get(resourceIden);
        if (coll == null)
            return;

        if (coll.size() == 1) {
            accessMap.remove(resourceIden);
            return;
        }

        coll.remove(Thread.currentThread());
    }

    // class lookup

    private void putClass(String methodPrefix, List<Param<?>> methodParams, Callback<Class<?>> cb, Supplier<Class<?>> delegate) {
        cb.self()
                // try parent
                .map(ClassLoader.class::cast)
                .map(ClassLoader::getParent)
                .map(parent -> ReflectUtil.icall(Class.class, parent, methodPrefix + "Class()", methodParams).orElse(null))

                // try delegate
                .or(() -> Optional.ofNullable(delegate.get()))

                // put if success
                .ifPresent(cb::setReturnValue);
    }


    // - actual hooks -

    // class finding/loading hooks

    private void hook_findClass(String name, Callback<Class<?>> cb) {
        if (!enter(0, "CLASS: " + name) || !checkClassRequest(cb.self().orElseThrow(), name))
            return;

        putClass("find", Param.single(String.class, name), cb,
                () -> delegation.findClass(name));

        leave(0, "CLASS: " + name);
    }

    private void hook_findClass(String module, String name, Callback<Class<?>> cb) {
        if (!enter(1, "CLASS: " + name) || !checkClassRequest(cb.self().orElseThrow(), name))
            return;

        putClass("find", Param.listOf(String.class, module, String.class, name),
                cb, () -> delegation.findClass(module, name));

        leave(1, "CLASS: " + name);
    }

    private void hook_loadClass(String name, boolean resolve, Callback<Class<?>> cb) {
        if (!enter(2, "CLASS: " + name) || !checkClassRequest(cb.self().orElseThrow(), name))
            return;

        putClass("load", Param.listOf(String.class, name, boolean.class, resolve),
                cb, () -> delegation.loadClass(name, resolve));

        leave(2, "CLASS: " + name);
    }

    // resource finding hooks

    private void hook_findResource(String name, Callback<URL> cb) {
        if (!enter(3, "RESOURCE: " + name) || !checkCl(cb.self().orElseThrow()))
            return;

        var u = delegation.findResource(name);
        if (u != null) {
            cb.setReturnValue(u);
        }

        leave(3, "RESOURCE: " + name);
    }

    private void hook_findResource(String module, String name, Callback<URL> cb) {
        if (!enter(4, "RESOURCE: " + name) || !checkCl(cb.self().orElseThrow()))
            return;

        var u = delegation.findResource(module, name);
        if (u != null) {
            cb.setReturnValue(u);
        }

        leave(4, "RESOURCE: " + name);
    }
}
