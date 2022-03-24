package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.util.Conditions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Hooks("java.util.Properties")
public class PropertiesLock {

    public static void lockSystemProperties(String... locks) {
        new HookInjection()
                .addHook(PropertiesLock.class, new PropertiesLock(System.getProperties(),
                        o -> o instanceof String s && Conditions.in(s, (Object[]) locks)))
                .inject().getCaught().ifPresent(cause -> {
                    throw new RuntimeException("Properties lock failed", cause);
                });
    }


    private final Properties self;
    private final Predicate<Object> isLocked;

    private RuntimeException _thr() {
        return new SecurityException("protected entry");
    }

    private void thr(Callback<?> cb) {
        cb.throwNow(_thr());
    }

    private void testAndThrow(Object key, Callback<?> cb) {
        if (cb.self() != this.self || !isLocked.test(key))
            return;

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

        if (t.keySet().stream().anyMatch(isLocked))
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
                throw _thr();
            it.remove();
        }
    }
}
