package eu.software4you.ulib.impl.dependencies;

import eu.software4you.transform.Callback;
import eu.software4you.transform.HookInjector;
import eu.software4you.transform.HookPoint;
import eu.software4you.ulib.ULib;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
final class DependencyInjector {
    private final ClassLoaderHook hook;
    private final List<Class<?>> hookedFindClass = new ArrayList<>();
    private final List<Class<?>> hookedLoadClass = new ArrayList<>();

    boolean acceptable(ClassLoader cl) {
        // no check for root CL bc it's abstract
        return cl != ClassLoader.getSystemClassLoader()
                && !(cl instanceof DependencyClassLoader);
    }

    // hooks into a classloader
    void inject(Class<? extends ClassLoader> clazz) {
        hookFindClass(clazz);
        hookLoadClass(clazz);
    }

    void purge(ClassLoader cl) {
        hook.purge(cl);
        hookedFindClass.remove(cl.getClass());
        hookedLoadClass.remove(cl.getClass());
    }

    /* findClass(String) */
    private void hookFindClass(Class<? extends ClassLoader> clazz) {
        findFirstUnderlying(clazz, hookedFindClass, "findClass", String.class)
                .ifPresent(this::hookFindClass0); // inject :D
    }

    @SneakyThrows
    private void hookFindClass0(Method into) {
        Method hook = ClassLoaderHook.class.getDeclaredMethod("findClass", String.class, Callback.class);
        HookInjector.directHook(hook, this.hook, into, HookPoint.HEAD);
    }

    /* loadClass(String, boolean) */

    private void hookLoadClass(Class<? extends ClassLoader> clazz) {
        findFirstUnderlying(clazz, hookedLoadClass, "loadClass", String.class, boolean.class)
                .ifPresent(this::hookLoadClass0); // inject :D
    }

    @SneakyThrows
    private void hookLoadClass0(Method into) {
        Method hook = ClassLoaderHook.class.getDeclaredMethod("loadClass", String.class, boolean.class, Callback.class);
        HookInjector.directHook(hook, this.hook, into, HookPoint.HEAD);
    }

    private Optional<Method> findFirstUnderlying(Class<? extends ClassLoader> cl, List<Class<?>> reg, String mName, Class<?>... mParams) {
        var scl = ClassLoader.getSystemClassLoader().getClass();
        Class<?> superclass;

        do {
            if (cl == ClassLoader.class || cl == scl || cl == DependencyClassLoader.class) // do not hook into root CL, system CL or DCL
                continue;

            Method m;
            try {
                m = cl.getDeclaredMethod(mName, mParams);
            } catch (NoSuchMethodException e) {
                // class does not override findClass(String)
                continue;
            }
            // method found
            if (reg.contains(cl)) { // return empty optional if class is already injected
                return Optional.empty();
            }
            reg.add(cl);
            ULib.logger().finer(() -> String.format("Flagging %s for dependency injection", m));
            return Optional.of(m);
        } while ((superclass = cl.getSuperclass()) != null // verify we still have a superclass
                // verify we're still within a (sub)class of CL
                && (cl = (Class<? extends ClassLoader>) superclass).isAssignableFrom(ClassLoader.class)
        );

        return Optional.empty();
    }
}
