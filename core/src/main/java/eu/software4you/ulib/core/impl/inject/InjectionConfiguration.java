package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.function.BiParamTask;
import eu.software4you.ulib.core.inject.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

import static eu.software4you.ulib.core.util.Conditions.in;

@RequiredArgsConstructor
@Getter
public class InjectionConfiguration {

    private final Class<?> targetClass;
    private final Map<String, Hooks<?>> hooks = new HashMap<>();


    @SuppressWarnings("unchecked")
    public <R> InjectionConfiguration with(String targetMethodSignature, Spec spec, BiParamTask<? super Object[], ? super Callback<R>, ?> call) {
        String signature;
        if ((signature = validateTarget(targetMethodSignature)) == null)
            throw new IllegalArgumentException("Hook target `%s` not found in %s".formatted(targetMethodSignature, targetClass.getName()));

        var cont = ((Hooks<R>) hooks.computeIfAbsent(signature, sig -> new Hooks<>()));
        var at = spec.point();

        switch (at) {
            case HEAD, RETURN -> cont.addCall(at, call);
            case METHOD_CALL, FIELD_READ, FIELD_WRITE ->
                    cont.addProxy(at, spec.target(), spec.n(), (BiParamTask<? super Object[], ? super Callback<?>, ?>) call);
            default -> throw new InternalError();
        }

        return this;
    }

    private String validateTarget(String targetMethodSignature) {
        if (targetMethodSignature.startsWith("<init>")) {

            var descriptor = Objects.requireNonNull(InjectionSupport.splitSignature(targetMethodSignature).getSecond());
            if (descriptor.isBlank()) {
                targetMethodSignature += descriptor = InjectionSupport.getDescriptor(targetClass.getDeclaredConstructors()[0]);
            }


            if (Arrays.stream(targetClass.getDeclaredConstructors())
                    .map(InjectionSupport::getDescriptor)
                    .noneMatch(descriptor::equals))
                return null;

            return targetMethodSignature;

        } else if (Arrays.stream(targetClass.getDeclaredMethods())
                .map(InjectionSupport::getSignature)
                .noneMatch(targetMethodSignature::equals))
            return null;

        return targetMethodSignature;
    }


    /**
     * Houses method specific hooks
     */
    @Getter
    static final class Hooks<R> {
        // hook point -> calls
        private final Map<Integer, Set<BiParamTask<? super Object[], ? super Callback<R>, ?>>> callbacks = new HashMap<>();

        // hook point -> ( target method/field signature (full) -> ( occurrence/n -> calls ) )
        // full signature: sig of class + sig of method/field
        private final Map<Integer, Map<String, Map<Integer, Set<BiParamTask<? super Object[], ? super Callback<?>, ?>>>>> proxyCallbacks = new HashMap<>();

        private void addCall(HookPoint at, BiParamTask<? super Object[], ? super Callback<R>, ?> call) {
            if (!in(at, HookPoint.HEAD, HookPoint.RETURN))
                throw new IllegalArgumentException();

            callbacks.computeIfAbsent(at.ordinal(), o -> new LinkedHashSet<>())
                    .add(call);
        }

        private void addProxy(HookPoint at, String target, int[] ns, BiParamTask<? super Object[], ? super Callback<?>, ?> call) {
            if (!in(at, HookPoint.METHOD_CALL, HookPoint.FIELD_READ, HookPoint.FIELD_WRITE))
                throw new IllegalArgumentException();

            var proxyMap = proxyCallbacks.computeIfAbsent(at.ordinal(), i -> new HashMap<>());
            var callsMap = proxyMap.computeIfAbsent(target, sig -> new HashMap<>());
            for (int n : ns) {
                var calls = callsMap.computeIfAbsent(n, i -> new LinkedHashSet<>());
                calls.add(call);
            }
        }
    }
}
