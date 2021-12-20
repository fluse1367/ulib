package eu.software4you.ulib.minecraft.api.usercache;

import eu.software4you.ulib.core.ULib;
import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import lombok.SneakyThrows;


/**
 * A central user cache. Needs to be enabled first.<br>
 * Automatically caches/updates usernames/UUIDs.
 */
public abstract class MainUserCache {
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

        mainCache = Providers.get(MainUserCache.class).enable0();
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
