package eu.software4you.ulib.loader.impl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.impl.FabricLoaderImpl;

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
        private final ExposedEnvironment exposedEnvironment;

        Environment(String... modules) {
            this(Collections.emptyMap(), modules);
        }

        Environment(Map<String, Collection<Class<?>[]>> hooks, String... modules) {
            this.hooks = hooks;
            this.modules = Set.of(modules);
            this.exposedEnvironment = new ExposedEnvironment(this);
        }

        public Map<String, Collection<Class<?>[]>> getAdditionalClassLoaderHookings() {
            return hooks;
        }

        public eu.software4you.ulib.loader.install.Environment asExposed() {
            return exposedEnvironment;
        }
    }

    @RequiredArgsConstructor
    private static final class ExposedEnvironment implements eu.software4you.ulib.loader.install.Environment {
        private final Environment env;

        @Override
        public String getName() {
            return env.name();
        }

        @Override
        public Collection<String> getModules() {
            return env.modules;
        }

        @Override
        public boolean isStandalone() {
            return switch (env) {
                case STANDALONE, STANDALONE_MINECRAFT -> true;
                default -> false;
            };
        }

        @Override
        public boolean isMinecraft() {
            return env.modules.contains("minecraft");
        }

        @Override
        public boolean isBungeecord() {
            return env.modules.contains("bungeecord");
        }

        @Override
        public boolean isVelocity() {
            return env.modules.contains("velocity");
        }

        @Override
        public boolean isSpigot() {
            return env.modules.contains("spigot");
        }

        @Override
        public boolean isFabric() {
            return env.modules.contains("fabric");
        }

        @Override
        public boolean isServer() {
            return isVelocity() || isBungeecord() || isSpigot() || !isClient();
        }

        @Override
        public boolean isClient() {
            return env == Environment.FABRIC && FabricLoaderImpl.INSTANCE.getEnvironmentType() == EnvType.CLIENT;
        }
    }
}
