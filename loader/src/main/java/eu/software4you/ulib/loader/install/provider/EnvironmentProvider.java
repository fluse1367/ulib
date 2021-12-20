package eu.software4you.ulib.loader.install.provider;

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
        SPIGOT, // 0
        BUNGEECORD, // 1
        VELOCITY, // 2
        STANDALONE, // 3
    }
}
