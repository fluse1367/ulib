package eu.software4you.ulib.loader.impl;

import lombok.Getter;

import java.util.*;

public class EnvironmentProvider {

    private static final String MOD_DEF = "core", MOD_ADD = "minecraft";

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
        SPIGOT(Map.of("loadClass0", Collections.singletonList(new Class<?>[]{String.class, boolean.class, boolean.class, boolean.class})),
                MOD_DEF, MOD_ADD, "spigot"),
        BUNGEECORD(MOD_DEF, MOD_ADD, "bungeecord"),
        VELOCITY(MOD_DEF, MOD_ADD, "velocity"),
        FABRIC(MOD_DEF, MOD_ADD),
        STANDALONE_MINECRAFT(MOD_DEF, MOD_ADD),
        STANDALONE(MOD_DEF),
        TEST(MOD_DEF, MOD_ADD, "spigot", "bungeecord", "velocity"),
        ;

        @Getter
        private final Collection<String> modules;
        private final Map<String, Collection<Class<?>[]>> hooks;

        Environment(String... modules) {
            this(Collections.emptyMap(), modules);
        }

        Environment(Map<String, Collection<Class<?>[]>> hooks, String... modules) {
            this.hooks = hooks;
            this.modules = Set.of(modules);
        }

        public Map<String, Collection<Class<?>[]>> getAdditionalClassLoaderHookings() {
            return hooks;
        }
    }
}
