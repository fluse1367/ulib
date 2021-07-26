package eu.software4you.ulib.impl.transform;

import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

final class HookRunner {
    private static final Map<String, Map<Integer, List<Entry<Object, Method>>>> hooks = new ConcurrentHashMap<>();

    static {
        // loading Map$Entry at <clinit> to prevent infinite loop when self-transforming class loader
        //noinspection ResultOfMethodCallIgnored
        Map.Entry.class.getName();
    }

    synchronized static void addHook(Method source, Object sourceInst, String fullDescriptor, int at) {

        if (!hooks.containsKey(fullDescriptor)) {
            hooks.put(fullDescriptor, new ConcurrentHashMap<>());
        }

        var map = hooks.get(fullDescriptor);
        if (!map.containsKey(at)) {
            map.put(at, new ArrayList<>());
        }

        var methods = map.get(at);
        methods.add(new AbstractMap.SimpleEntry<>(sourceInst, source));
    }

    /**
     * Removes non-static hooks
     *
     * @param sourceInst the instances to remove
     * @return a map containing hooks unused after deletion (className, descriptors)
     */
    static Map<String, List<String>> delHooks(Object sourceInst) {
        hooks.forEach((hookId, hookPoints) -> {
            hookPoints.forEach((hookPoint, methods) -> methods.removeIf(pair ->
                    pair.getKey() == sourceInst));
            hookPoints.values().removeIf(List::isEmpty); // gc
        });
        return gcHooks();
    }

    /**
     * Removes static hooks
     *
     * @param clazz the hooks class
     * @return a map containing hooks unused after deletion (className, descriptors)
     */
    static Map<String, List<String>> delHooks(Class<?> clazz) {
        hooks.forEach((hookId, hookPoints) -> {
            hookPoints.forEach((hookPoint, methods) -> methods.removeIf(pair ->
                    pair.getValue().getDeclaringClass() == clazz));
            hookPoints.values().removeIf(List::isEmpty); // gc
        });
        return gcHooks();
    }

    /**
     * Removes all unnecessary elements in the hook map structure
     *
     * @return a map containing hooks unused after deletion (className, descriptors)
     */
    private static Map<String, List<String>> gcHooks() {
        hooks.forEach((fullDesc, hookPoints) -> hookPoints.values().removeIf(List::isEmpty));

        Map<String, List<String>> removed = new HashMap<>();

        hooks.entrySet().removeIf(en -> {
            String fullDesc = en.getKey();
            var map = en.getValue();
            if (map.isEmpty()) {
                // resolve fullDesc
                String className = fullDesc.substring(0, fullDesc.lastIndexOf("."));
                String desc = fullDesc.substring(className.length());
                if (!removed.containsKey(className)) {
                    removed.put(className, new ArrayList<>(Collections.singletonList(desc)));
                } else {
                    removed.get(className).add(desc);
                }
                return true;
            }
            return false;
        });

        return removed;
    }

    static void delHook(Method source, Object sourceInst, String fullDescriptor, int at) {
        if (!hooks.containsKey(fullDescriptor))
            return;
        var hookPoints = hooks.get(fullDescriptor);
        if (hookPoints.containsKey(at)) {
            var li = hookPoints.get(at);
            li.removeIf(pair -> pair.getKey() == sourceInst && pair.getValue() == source);
            if (li.isEmpty()) {
                hookPoints.remove(at); // gc
                if (hookPoints.isEmpty()) {
                    hooks.remove(fullDescriptor); // gc
                }
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SneakyThrows
    static Callback<?> runHooks(Class<?> returnType, Object returnValue, boolean hasReturnValue, Object self, Class<?> caller, String hookId, int at, Object[] params) {
        Callback<?> cb = new Callback(returnType, returnValue, hasReturnValue, self, caller);
        if (!hooks.containsKey(hookId))
            return cb;

        var map = hooks.get(hookId);
        if (!map.containsKey(at))
            return cb;

        Object[] args = new Object[params.length + 1];
        args[params.length] = cb;
        System.arraycopy(params, 0, args, 0, params.length);

        for (Entry<Object, Method> hook : map.get(at)) {
            hook.getValue().invoke(hook.getKey(), args);
            if (cb.isCanceled())
                break; // cancel all future hook processing
        }

        return cb;
    }
}
