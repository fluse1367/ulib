package eu.software4you.ulib.core.impl.inject;

import eu.software4you.ulib.core.function.BiParamTask;
import eu.software4you.ulib.core.inject.Callback;
import eu.software4you.ulib.core.inject.HookPoint;
import lombok.*;

import java.util.*;

@RequiredArgsConstructor
@Getter
public class InjectionConfiguration {

    private final Class<?> targetClass;
    private final Map<String, Hooks<?>> hooks = new HashMap<>();


    public <R> InjectionConfiguration with(String targetMethod, HookPoint at, BiParamTask<? super Object[], ? super Callback<R>, ?> call) {
        //noinspection unchecked
        ((Hooks<R>) hooks.computeIfAbsent(targetMethod, Hooks::new))
                .with(at, call);
        return this;
    }


    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    final class Hooks<R> {
        private final String targetMethod; // JNI descriptor
        @Getter
        private final Map<Integer, Collection<BiParamTask<? super Object[], ? super Callback<R>, ?>>> callbacks = new HashMap<>();

        private void with(HookPoint at, BiParamTask<? super Object[], ? super Callback<R>, ?> call) {
            if (Arrays.stream(targetClass.getDeclaredMethods())
                    .map(InjectionSupport::getSignature)
                    .noneMatch(targetMethod::equals))
                throw new IllegalArgumentException("Hook target `%s` not found in %s".formatted(targetMethod, targetClass.getName()));

            callbacks.computeIfAbsent(at.ordinal(), ArrayList::new)
                    .add(call);
        }
    }
}
