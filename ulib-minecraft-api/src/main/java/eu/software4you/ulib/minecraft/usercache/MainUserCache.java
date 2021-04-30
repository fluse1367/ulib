package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.reflect.ReflectUtil;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;


public abstract class MainUserCache {
    @Await
    private static MainUserCache impl;
    private static UserCache mainCache;

    public static boolean isEnabled() {
        return mainCache != null;
    }

    @SneakyThrows
    public static void enable() {
        if (isEnabled())
            return;
        ULib.logger().finer(() -> "Enabling main user cache! Enabled by " + ReflectUtil.getCallerClassName());

        mainCache = impl.enable0();
    }

    public static UserCache get() {
        return mainCache;
    }

    protected abstract UserCache enable0();
}
