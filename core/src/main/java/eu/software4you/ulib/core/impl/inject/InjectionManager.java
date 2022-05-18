package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.function.BiParamTask;
import eu.software4you.ulib.core.impl.Internal;
import eu.software4you.ulib.core.impl.inject.InjectionConfiguration.Hooks;
import eu.software4you.ulib.core.inject.*;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import lombok.*;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InjectionManager {
    public static final String HOOKING_KEY = "ulib.hooking", PROXY_KEY = "ulib.hook_proxy";

    @Getter
    private static final InjectionManager instance = new InjectionManager();

    private static final Exception DECLARE_SUCCESS = new Exception(); // a throwable that indicates nothing was thrown
    @Getter
    private final Map<Thread, Throwable> transformThrowings = new HashMap<>();

    static {
        System.getProperties().put(HOOKING_KEY, new Object[]{
                // [0] Hook runner
                (Function<Object[], ?>) instance::runHooks,

                // [1] Callback#isReturning()
                (Function<CallbackImpl<?>, Boolean>) CallbackImpl::isReturning,

                // [2] Callback#getReturnValue()
                (Function<Callback<?>, ?>) Callback::getReturnValue,

                // [3] caller class determination
                (Supplier<Class<?>>) ReflectUtil::getCallerClass
        });

        System.getProperties().put(PROXY_KEY, new Object[]{
                // [0] Proxy runner
                (Function<Object[], ?>) instance::runProxies,

                // [1] Callback#isReturning()
                (Function<CallbackImpl<?>, Boolean>) CallbackImpl::isReturning,

                // [2] Callback#hasReturnValue()
                (Function<Callback<?>, ?>) Callback::hasReturnValue,

                // [3] Callback#getReturnValue()
                (Function<Callback<?>, ?>) Callback::getReturnValue,

                // [4] caller class determination
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Callback<?> runHooks(Class<?> clazz, Class<?> returnType, Object returnValue, boolean hasReturnValue,
                                 Object self, Class<?> caller, String methodDescriptor, int at, Object[] params) {
        CallbackImpl<?> cb = new CallbackImpl(returnType, returnValue, hasReturnValue, self, caller);

        var calls = Optional.ofNullable(injected.get(clazz))
                .map(conf -> conf.getHooks().get(methodDescriptor))
                .map(Hooks::getCallbacks)
                .map(map -> map.get(at))
                .orElse(Collections.emptySet());

        return processCalls((Set) calls, params, cb);
    }

    private Callback<?> runProxies(Object[] params) {
        return runProxies(
                ReflectUtil.getCallerClass(),
                (Class<?>) params[0],
                params[1],
                (boolean) params[2],
                params[3],
                params[4],
                (Class<?>) params[5],
                (String) params[6],
                (String) params[7],
                (int) params[8],
                (int) params[9],
                (Object[]) params[10]
        );
    }

    private Callback<?> runProxies(Class<?> clazz, Class<?> resultType, Object initialValue, boolean hasInitialValue,
                                   Object self, Object proxyInst, Class<?> caller,
                                   String methodSignature, String fullTargetSignature, int n, int at, Object[] params) {
        @SuppressWarnings({"unchecked", "rawtypes"})
        CallbackImpl<?> cb = new CallbackImpl(resultType, initialValue, hasInitialValue, self, proxyInst, caller);

        var calls = Optional.ofNullable(injected.get(clazz))
                .map(spec -> spec.getHooks().get(methodSignature))
                .map(cont -> cont.getProxyCallbacks().get(at))
                .map(proxyMap -> proxyMap.get(fullTargetSignature))
                .map(callsMap -> {
                    // region collect & merge calls with defaults
                    // collect default calls
                    var defaultCalls = Optional.ofNullable(callsMap.get(0))
                            .orElse(Collections.emptySet());
                    var additionalCalls = Optional.ofNullable(callsMap.get(n))
                            .orElse(Collections.emptySet());

                    if (defaultCalls.isEmpty() && additionalCalls.isEmpty())
                        return null;

                    Set<BiParamTask<? super Object[], ? super Callback<?>, ?>> merged = new LinkedHashSet<>(defaultCalls.size() + additionalCalls.size(), 1);
                    merged.addAll(defaultCalls);
                    merged.addAll(additionalCalls);
                    return merged;
                    // endregion
                })
                .orElse(Collections.emptySet());

        return processCalls(calls, params, cb);
    }

    @SneakyThrows
    private Callback<?> processCalls(Set<BiParamTask<? super Object[], ? super Callback<?>, ?>> calls, Object[] params, CallbackImpl<?> callback) {
        for (var call : calls) {
            try {
                //noinspection RedundantCast,unchecked
                ((BiParamTask<Object, Object, Exception>) call).execute(params.clone(), callback);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof HookException he)
                    throw he.getCause();
                throw e.getCause();
            } catch (HookException he) {
                throw he.getCause();
            }
            if (callback.isCanceled())
                break; // cancel all future hook processing
        }

        return callback;
    }

    boolean shouldProcess(Class<?> clazz) {
        return injected.containsKey(clazz);
    }

    Collection<String> getTargetMethods(Class<?> clazz) {
        return injected.get(clazz).getHooks().keySet();
    }

    boolean shouldProxy(Class<?> clazz, String methodSignature, HookPoint where, String targetSignature, int n) {
        var conf = injected.get(clazz);
        if (conf == null)
            return false;

        var cont = conf.getHooks().get(methodSignature);
        if (cont == null)
            return false;

        return Optional.ofNullable(cont.getProxyCallbacks().get(where.ordinal()))
                .map(proxyMap -> proxyMap.get(targetSignature))
                .map(cbMap -> cbMap.containsKey(0) || cbMap.containsKey(n))
                .orElse(false);
    }

    void ensureProxySatisfaction(Class<?> clazz, String methodSignature, Map<HookPoint, Map<String, Collection<Integer>>> done) throws ConfigurationSatisfactionException {

        var cont = Optional.ofNullable(injected.get(clazz))
                .map(conf -> conf.getHooks().get(methodSignature))
                .orElseThrow(IllegalArgumentException::new);


        for (var en : cont.getProxyCallbacks().entrySet()) {
            var point = HookPoint.values()[en.getKey()];

            if (!done.containsKey(point))
                throw new ConfigurationSatisfactionException("Hook Point %s not found in `%s`"
                        .formatted(point, methodSignature));
            var doneTargets = done.get(point);

            var targets = en.getValue();

            for (var targetEn : targets.entrySet()) {
                var target = targetEn.getKey();

                if (!doneTargets.containsKey(target))
                    throw new ConfigurationSatisfactionException("Target `%s` for %s not found in %s"
                            .formatted(target, point, methodSignature));
                var doneNs = doneTargets.get(target);

                for (int n : targetEn.getValue().keySet()) {
                    if (n == 0) { // 0 means all
                        if (doneNs.isEmpty()) // throw if not a single injection happened
                            throw new ConfigurationSatisfactionException("No occurrence of target `%s`for %s found in %s"
                                    .formatted(target, point, methodSignature));
                        continue;
                    }

                    if (!doneNs.contains(n))
                        throw new ConfigurationSatisfactionException("Nth occurrence (%d) of target `%s` for %s not found in %s"
                                .formatted(n, target, point, methodSignature));
                }

            }

        }
    }

    public void injectionsJoin(Map<Class<?>, InjectionConfiguration> instructions) throws Exception {
        // TODO: transition from merge operation in hard-code to dynamic code?
        var op = new MergeOp(this.injected, instructions);
        op.merge();

        var toRedefine = op.needsRedefinition.toArray(Class[]::new);
        if (toRedefine.length == 0)
            return;

        // redefine classes
        transformThrowings.put(Thread.currentThread(), DECLARE_SUCCESS);
        Internal.getInstrumentation().retransformClasses(toRedefine);

        var threw = transformThrowings.remove(Thread.currentThread());
        if (threw != DECLARE_SUCCESS && threw != null)
            throw new RuntimeException("Injection failed", threw);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static final class MergeOp {
        private final Map<Class<?>, InjectionConfiguration> existing;
        private final Map<Class<?>, InjectionConfiguration> merging;
        private final HashSet<Class<?>> needsRedefinition = new HashSet<>();
        private Class<?> currentClass;

        private void markRedefinition() {
            needsRedefinition.add(currentClass);
        }

        private void merge() {
            merging.forEach((clazz, mergingConf) -> {
                currentClass = clazz;

                if (!existing.containsKey(clazz)) {
                    existing.put(clazz, mergingConf);
                    markRedefinition();
                    return;
                }

                merge(existing.get(clazz), mergingConf);
            });

            currentClass = null;
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private void merge(InjectionConfiguration existingConf, InjectionConfiguration mergingConf) {
            mergingConf.getHooks().forEach((descriptor, mergingCont) -> {
                var existingHooks = existingConf.getHooks();
                if (!existingHooks.containsKey(descriptor)) {
                    existingHooks.put(descriptor, mergingCont);
                    markRedefinition();
                    return;
                }

                // raw type for compiler compatibility
                merge((Hooks) existingHooks.get(descriptor), (Hooks) mergingCont);
            });
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private <R> void merge(Hooks<R> existingCont, Hooks<R> mergingCont) {
            // raw type for compiler compatibility
            Map existingCallbacks = existingCont.getCallbacks();
            mergingCont.getCallbacks().forEach((at, mergingCallbacks) -> {
                if (!existingCallbacks.containsKey(at)) {
                    existingCallbacks.put(at, mergingCallbacks);
                    // no necessity to indicate redefinition bc regular hooks are always injected at HEAD & RETURN regardless of present callbacks
                    return;
                }

                ((Collection) existingCallbacks.get(at)).addAll(mergingCallbacks);

            });

            var existingProxyCallbacks = existingCont.getProxyCallbacks();
            mergingCont.getProxyCallbacks().forEach((at, mergingProxyMap) -> {
                if (!existingProxyCallbacks.containsKey(at)) {
                    existingProxyCallbacks.put(at, mergingProxyMap);
                    markRedefinition();
                    return;
                }

                var existingProxyMap = existingProxyCallbacks.get(at);
                mergingProxyMap.forEach((targetSignature, mergingCallMap) -> {
                    if (!existingProxyMap.containsKey(targetSignature)) {
                        existingProxyMap.put(targetSignature, mergingCallMap);
                        markRedefinition();
                        return;
                    }

                    var existingCallMap = existingProxyMap.get(targetSignature);
                    mergingCallMap.forEach((n, mergingCalls) -> {
                        if (!existingCallMap.containsKey(n)) {
                            existingCallMap.put(n, mergingCalls);
                            markRedefinition();
                            return;
                        }

                        existingCallMap.get(n).addAll(mergingCalls);

                    });

                });

            });
        }
    }

}
