package eu.software4you.ulib.impl.transform;

import eu.software4you.common.collection.Pair;
import eu.software4you.transform.HookPoint;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class Hooks {
    private static final Map<String, Map<HookPoint, List<Pair<Object, Method>>>> hooks = new ConcurrentHashMap<>();

    synchronized static void addHook(Method source, Object sourceInst, String fullDescriptor, HookPoint at) {

        if (!hooks.containsKey(fullDescriptor)) {
            hooks.put(fullDescriptor, new ConcurrentHashMap<>());
        }

        val map = hooks.get(fullDescriptor);
        if (!map.containsKey(at)) {
            map.put(at, new ArrayList<>());
        }

        val methods = map.get(at);
        methods.add(new Pair<>(sourceInst, source));
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
                    pair.getFirst() == sourceInst));
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
                    pair.getSecond().getDeclaringClass() == clazz));
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
            val map = en.getValue();
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

    static void delHook(Method source, Object sourceInst, String fullDescriptor, HookPoint at) {
        if (!hooks.containsKey(fullDescriptor))
            return;
        val hookPoints = hooks.get(fullDescriptor);
        if (hookPoints.containsKey(at)) {
            val li = hookPoints.get(at);
            li.removeIf(pair -> pair.getFirst() == sourceInst && pair.getSecond() == source);
            if (li.isEmpty()) {
                hookPoints.remove(at); // gc
                if (hookPoints.isEmpty()) {
                    hooks.remove(fullDescriptor); // gc
                }
            }
        }
    }

    @SneakyThrows
    public static void runHooks(String hookId, HookPoint at, Object[] params, Callback<?> cb) {
        if (!hooks.containsKey(hookId))
            return;

        Object[] args = new Object[params.length + 1];
        args[params.length] = cb;
        System.arraycopy(params, 0, args, 0, params.length);

        val map = hooks.get(hookId);
        if (!map.containsKey(at))
            return;

        for (Pair<Object, Method> hook : map.get(at)) {
            hook.getSecond().invoke(hook.getFirst(), args);
            if (cb.isCanceled())
                break; // cancel all future hook processing
        }

    }
}
