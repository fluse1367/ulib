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
    public static void runHook(int hookId, Object[] params, Callback<?> cb) {
        if (!hooks.containsKey(hookId))
            return;
        val hook = hooks.get(hookId);

        Object[] args = new Object[params.length + 1];
        args[params.length] = cb;
        System.arraycopy(params, 0, args, 0, params.length);

        hook.getFirst().invoke(hook.getSecond(), args);
    }
}
