package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.function.BiParamTask;
import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.inject.Callback;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InjectionManager {

    @Getter
    private static final InjectionManager instance = new InjectionManager();

    private static final Exception DECLARE_SUCCESS = new Exception(); // a throwable that indicates nothing was thrown
    @Getter
    private final Map<Thread, Throwable> transformThrowings = new HashMap<>();

    static {
        System.getProperties().put("ulib.hooking", new Object[]{
                /* [0] Hook runner */
                (Function<Object[], ?>) instance::runHooks,

                /* [1] Callback#isReturning() */
                (Function<CallbackImpl<?>, Boolean>) CallbackImpl::isReturning,

                /* [2] Callback#getReturnValue() */
                (Function<Callback<?>, ?>) Callback::getReturnValue,

                /* [3] caller class determination */
                (Supplier<Class<?>>) ReflectUtil::getCallerClass
        });
        Internal.getInstrumentation().addTransformer(new ClassTransformer(instance), true);
    }

    private final Map<Class<?>, InjectionConfiguration> injected = new HashMap<>();

    private Callback<?> runHooks(Object[] params) {
        return runHooks(
                ReflectUtil.getCallerClass(),
                (Class<?>) params[0],
                params[1],
                (boolean) params[2],
                params[3],
                (Class<?>) params[4],
                (String) params[5],
                (int) params[6],
                (Object[]) params[7]
        );
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @SneakyThrows
    private CallbackImpl<?> runHooks(Class<?> clazz, Class<?> returnType, Object returnValue, boolean hasReturnValue, Object self, Class<?> caller, String methodDescriptor, int at, Object[] params) {
        CallbackImpl<?> cb = new CallbackImpl(returnType, returnValue, hasReturnValue, self, caller);
        if (!injected.containsKey(clazz))
            return cb;

        var conf = injected.get(clazz);
        if (!conf.getHooks().containsKey(methodDescriptor))
            return cb;

        var callbacks = conf.getHooks().get(methodDescriptor).getCallbacks();
        if (!callbacks.containsKey(at))
            return cb;


        for (BiParamTask call : callbacks.get(at)) {
            try {
                call.execute(params, cb);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof HookException he)
                    throw he.getCause();
                throw e.getCause();
            }
            if (cb.isCanceled())
                break; // cancel all future hook processing
        }

        return cb;
    }

    boolean shouldProcess(Class<?> clazz) {
        return injected.containsKey(clazz);
    }

    Collection<String> getTargetMethods(Class<?> clazz) {
        return injected.get(clazz).getHooks().keySet();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void injectionsJoin(Map<Class<?>, InjectionConfiguration> instructions) throws Exception {
        Collection<Class<?>> needsRedefinition = new HashSet<>();

        instructions.forEach((cl, conf) -> {

            if (!injected.containsKey(cl)) {
                needsRedefinition.add(cl);
                injected.put(cl, conf);
                return;
            }

            var existingConf = injected.get(cl);

            conf.getHooks().forEach((descriptor, hooks) -> {
                var existingHooks = existingConf.getHooks();
                if (!existingHooks.containsKey(descriptor)) {
                    needsRedefinition.add(cl);
                    existingHooks.put(descriptor, hooks);
                    return;
                }

                // raw type for compiler compatibility
                Map existingCallbacks = existingHooks.get(descriptor).getCallbacks();

                hooks.getCallbacks().forEach((at, callbacks) -> {
                    if (!existingCallbacks.containsKey(at)) {
                        existingCallbacks.put(at, callbacks);
                        return;
                    }

                    ((Collection) existingCallbacks.get(at)).addAll(callbacks);

                });

            });

        });

        // redefine classes
        transformThrowings.put(Thread.currentThread(), DECLARE_SUCCESS);
        Internal.getInstrumentation().retransformClasses(needsRedefinition.toArray(Class[]::new));

        var threw = transformThrowings.remove(Thread.currentThread());
        if (threw != DECLARE_SUCCESS && threw != null)
            throw new RuntimeException("Injection failed", threw);
    }

}
