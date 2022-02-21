package eu.software4you.ulib.core.impl.transform;

import eu.software4you.ulib.core.api.transform.Callback;
import eu.software4you.ulib.core.api.transform.HookInjector;
import eu.software4you.ulib.core.api.transform.*;
import eu.software4you.ulib.core.impl.Agent;
import eu.software4you.ulib.supermodule.hooking.CallbackReference;
import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.AccessibleObject;
import java.security.ProtectionDomain;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccessibleObjectTransformer implements ClassFileTransformer {
    private static final String SUDO_KEY = "ulib.sudo";

    public static void init() {
        Set<Module> permitted = AccessibleObjectTransformer.class.getModule().getLayer().modules().stream()
                .filter(m -> m.getName().startsWith("ulib."))
                .collect(Collectors.toSet());
        System.getProperties().put(SUDO_KEY, (Predicate<Module>) permitted::contains);

        var agent = Agent.getInstance();
        agent.addTransformer(new AccessibleObjectTransformer());
        agent.transform(AccessibleObject.class);
    }

    @SneakyThrows
    public static void ulibAllAccessToUnnamed() {
        var method = Module.class.getDeclaredMethod("implAddReadsAllUnnamed");
        method.setAccessible(true);

        var modules = AccessibleObjectTransformer.class.getModule().getLayer().modules().stream()
                .filter(m -> m.getName().startsWith("ulib."))
                .toArray(Module[]::new);

        for (Module module : modules) {
            method.invoke(module);
        }
    }

    /**
     * Dummy test is required to trigger {@link Properties#remove(Object)} in {@link CallbackReference#putSelf()}.
     * Effectively the dummy is only present to initially load the static fields in the callback ref before the sudo lock gets installed.
     */
    @SuppressWarnings("JavadocReference")
    public static void dummyTest() {
        var dummy = new Dummy();
        HookInjector.hook(dummy);
        var res = System.getProperties().put("ulib-dummy-test", "n.a.");
        if (!(res instanceof String s && s.equals("SUCCESS")))
            throw new RuntimeException("dummy test failure");
        HookInjector.unhook(dummy);
    }

    public static void lockSudo() {
        HookInjector.hook(new SudoLock(System.getProperties(), o -> o instanceof String s && s.equals(SUDO_KEY)));
    }

    @Override
    public byte[] transform(ClassLoader loader, String clName, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] byteCode) throws IllegalClassFormatException {
        final String className = clName.replace('/', '.');
        if (!className.equals("java.lang.reflect.AccessibleObject"))
            return byteCode;


        try {
            ClassPool pool = new ClassPool(true);
            pool.appendClassPath(new LoaderClassPath(loader));
            pool.appendClassPath(new ByteArrayClassPath(className, byteCode));
            pool.importPackage("java.util.function");

            CtClass cc = pool.get(className);

            var cm = cc.getMethod("checkCanSetAccessible", "(Ljava/lang/Class;Ljava/lang/Class;)V");

            cm.insertBefore("""
                    if (((Predicate) System.getProperties().get((Object) "%s")).test($1.getModule())) {
                        return;
                    }
                    """.formatted(SUDO_KEY));

            return cc.toBytecode();
        } catch (Throwable thr) {
            thr.printStackTrace();
        }

        return byteCode;
    }

    @Hooks("java.util.Properties")
    public static final class Dummy {
        @Hook(value = "put")
        public void put(Object key, Object value, Callback<Object> cb) {
            if (key == "ulib-dummy-test")
                cb.setReturnValue("SUCCESS");
        }
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @Hooks("java.util.Properties")
    public static final class SudoLock {
        private final Properties self;
        private final Predicate<Object> isLocked;

        private void thr(Callback<?> cb) {
            cb.throwNow(new SecurityException("sudo lock"));
        }

        private void testAndThrow(Object key, Callback<?> cb) {
            if (cb.self() != this.self || !isLocked.test(key))
                return;

            System.out.println("Call from " + cb.callerClass());

            thr(cb);
        }

        @Hook(value = "remove(Ljava/lang/Object;)Ljava/lang/Object;")
        public void hook_remove(Object key, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "remove(Ljava/lang/Object;Ljava/lang/Object;)Z")
        public void hook_remove(Object key, Object value, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "put")
        public void hook_put(Object key, Object value, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "putAll")
        public void hook_putAll(Map<?, ?> t, Callback<Void> cb) {
            if (cb.self() != this.self)
                return;

            if (t.containsKey(SUDO_KEY))
                thr(cb);
        }

        @Hook(value = "compute")
        public void hook_compute(Object key, Object func, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "computeIfPresent")
        public void hook_computeIfPresent(Object key, Object func, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "replace(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;")
        public void hook_replace(Object key, Object newValue, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "replace(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Z")
        public void hook_replace(Object key, Object oldValue, Object newValue, Callback<Boolean> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "merge")
        public void hook_merge(Object key, Object val, Object func, Callback<Object> cb) {
            testAndThrow(key, cb);
        }

        @Hook(value = "entrySet", at = HookPoint.RETURN)
        public void hook_entrySet(Callback<Set<Map.Entry<Object, Object>>> cb) {
            if (cb.self() != this.self)
                return;

            var s = cb.getReturnValue();
            cb.setReturnValue(new AbstractSet<>() {
                @Override
                public Iterator<Map.Entry<Object, Object>> iterator() {
                    return new It<>(s.iterator(), e -> e, Map.Entry::getKey);
                }

                @Override
                public int size() {
                    return s.size();
                }
            });
        }

        @Hook(value = "keySet", at = HookPoint.RETURN)
        public void hook_keySet(Callback<Set<Object>> cb) {
            if (cb.self() != this.self)
                return;

            var s = cb.getReturnValue();
            cb.setReturnValue(new AbstractSet<Object>() {
                @Override
                public Iterator<Object> iterator() {
                    return new It<>(s.iterator(), o -> o, o -> o);
                }

                @Override
                public int size() {
                    return s.size();
                }
            });
        }

        @Hook(value = "values")
        public void hook_values(Callback<Collection<Object>> cb) {
            if (cb.self() != this.self)
                return;

            var s = self.entrySet();
            cb.setReturnValue(new AbstractCollection<Object>() {
                @Override
                public Iterator<Object> iterator() {
                    return new It<>(s.iterator(), Map.Entry::getValue, Map.Entry::getKey);
                }

                @Override
                public int size() {
                    return s.size();
                }
            });
        }

        @Hook(value = "keys")
        public void hook_keys(Callback<Enumeration<Object>> cb) {
            if (cb.self() != this.self)
                return;
            cb.setReturnValue(Collections.enumeration(self.keySet()));
        }

        @Hook(value = "elements")
        public void hook_elements(Callback<Enumeration<Object>> cb) {
            if (cb.self() != this.self)
                return;
            cb.setReturnValue(Collections.enumeration(self.values()));
        }

        @Hook(value = "clear")
        public void hook_clear(Callback<Void> cb) {
            if (cb.self() != this.self)
                return;

            var s = self.entrySet();
            s.removeIf(e -> !isLocked.test(e.getKey()));
            cb.cancel();
        }

        @RequiredArgsConstructor
        private final class It<T, R> implements Iterator<R> {
            private final Iterator<T> it;
            private final Function<T, R> converter;
            private final Function<T, Object> keyer;
            private T current;

            @Override
            public boolean hasNext() {
                return it.hasNext();
            }

            @Override
            public R next() {
                return converter.apply(current = it.next());
            }

            @Override
            public void remove() {
                if (current == null)
                    return;
                if (isLocked.test(keyer.apply(current)))
                    throw new SecurityException("sudo lock");
                it.remove();
            }
        }
    }
}
