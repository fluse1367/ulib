package eu.software4you.ulib.impl.litetransform;

import eu.software4you.common.collection.Pair;
import lombok.SneakyThrows;
import lombok.val;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class Hooks {
    private static final Map<Integer, Pair<Method, Object>> hooks = new ConcurrentHashMap<>();

    static int addHook(Method method, Object obj) {
        int id = hooks.size();
        hooks.put(id, new Pair<>(method, obj));
        return id;
    }

    @SneakyThrows
    public static void runHook(int hookId, Object[] params) {
        if (!hooks.containsKey(hookId))
            return;
        val hook = hooks.get(hookId);
        hook.getFirst().invoke(hook.getSecond(), params);
    }
}
