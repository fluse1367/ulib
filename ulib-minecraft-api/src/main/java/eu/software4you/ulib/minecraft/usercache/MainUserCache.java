package eu.software4you.ulib.minecraft.usercache;

import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ULib;
import lombok.SneakyThrows;


/**
 * A central user cache. Needs to be enabled first.<br>
 * Automatically caches/updates usernames/UUIDs.
 */
public abstract class MainUserCache {
    @Await
    private static MainUserCache impl;
    private static UserCache mainCache;

    /**
     * Checks if the main user cache is enabled.
     *
     * @return {@code true}, if it is enabled
     */
    public static boolean isEnabled() {
        return mainCache != null;
    }

    /**
     * Enables the main user cache.
     */
    @SneakyThrows
    public static void enable() {
        if (isEnabled())
            return;
        ULib.logger().finer(() -> "Enabling main user cache! Enabled by " + ReflectUtil.getCallerClass().getName());

        mainCache = impl.enable0();
    }

    /**
     * Obtains the main user cache instance.
     *
     * @return the main user cache, or {@code null}, if it is not enabled
     * @see #enable()
     * @see #isEnabled()
     */
    public static UserCache get() {
        return mainCache;
    }

    protected abstract UserCache enable0();
}
