package eu.software4you.ulib.loader.install.provider;

import java.util.Map;
import java.util.Objects;

public class EnvironmentProvider {

    private static Environment env;

    public static void initAs(Environment env) {
        if (EnvironmentProvider.env != null)
            throw new IllegalStateException("Environment already initialized");
        EnvironmentProvider.env = Objects.requireNonNull(env);
    }

    public static Environment get() {
        return Objects.requireNonNullElse(env, Environment.STANDALONE);
    }

    public enum Environment {
        /* 0 */ SPIGOT(Map.of("loadClass0", new Class<?>[]{String.class, boolean.class, boolean.class, boolean.class})),
        /* 1 */ BUNGEECORD(),
        /* 2 */ VELOCITY(),
        /* 3 */ STANDALONE(),
        ;

        private final Map<String, Class<?>[]> hooks;

        Environment() {
            this(Map.of());
        }

        Environment(Map<String, Class<?>[]> hooks) {
            this.hooks = hooks;
        }

        public Map<String, Class<?>[]> getAdditionalClassLoaderHookings() {
            return hooks;
        }
    }
}
