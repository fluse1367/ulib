package eu.software4you.ulib.core.impl.delegation;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.transform.Callback;
import eu.software4you.ulib.core.api.transform.HookInjector;
import eu.software4you.ulib.core.api.transform.HookPoint;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Map;

public final class DelegationInjector {

    private final DelegationHook delegation;
    private final Method hookFindClass, hookFindAdditionalClass, hookFindModuleClass, hookLoadClass;
    private final Map<String, Class<?>[]> additionalHooks;

    @SneakyThrows
    public DelegationInjector(DelegationHook delegation, Map<String, Class<?>[]> additionalHooks) {
        this.delegation = delegation;
        this.additionalHooks = additionalHooks;
        var c = DelegationHook.class;
        this.hookFindClass = c.getMethod("hookFindClass", String.class, Callback.class);
        this.hookFindModuleClass = c.getMethod("hookFindClass", String.class, String.class, Callback.class);
        this.hookLoadClass = c.getMethod("hookLoadClass", String.class, boolean.class, Callback.class);
        this.hookFindAdditionalClass = c.getMethod("hookFindClassAdditional", Object[].class, Callback.class);
        ULib.logger().finer("Delegation Injector init with delegation hook: " + delegation);
    }

    public void inject(Class<? extends ClassLoader> clazz) {
        ReflectUtil.findUnderlyingMethod(clazz, "findClass", true, String.class)
                .ifPresent(into -> inject(hookFindClass, into));
        ReflectUtil.findUnderlyingMethod(clazz, "findClass", true, String.class, String.class)
                .ifPresent(into -> inject(hookFindModuleClass, into));
        ReflectUtil.findUnderlyingMethod(clazz, "loadClass", true, String.class, boolean.class)
                .ifPresent(into -> inject(hookLoadClass, into));

        additionalHooks.forEach((name, params) -> ReflectUtil
                .findUnderlyingMethod(clazz, name, true, params)
                .ifPresent(into -> inject(hookFindAdditionalClass, into)));
    }

    private void inject(Method hook, Method into) {
        HookInjector.directHook(hook, this.delegation, into, HookPoint.HEAD);
    }
}
