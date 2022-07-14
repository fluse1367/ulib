package eu.software4you.ulib.loader.install;

import java.util.Collection;

/**
 * Represents the environment ulib is running in.
 */
public interface Environment {

    /**
     * @return the environment name.
     */
    String getName();

    /**
     * @return the running ulib modules
     */
    Collection<String> getModules();

    /**
     * Checks if the environment is considered standalone.
     *
     * @return {@code true}, if this environment is running in standalone mode, {@code false} otherwise
     */
    boolean isStandalone();

    /**
     * Checks if the environment is considered a minecraft-context environment, meaning the minecraft module is available.
     *
     * @return {@code true}, if this environment is running in a minecraft context mode, {@code false} otherwise
     */
    boolean isMinecraft();

    /**
     * Checks if the environment is considered a bungeecord environment, meaning the bungeecord module is available.
     *
     * @return {@code true}, if this environment is running in bungeecord mode, {@code false} otherwise
     */
    boolean isBungeecord();

    /**
     * Checks if the environment is considered a velocity environment, meaning the velocity module is available.
     *
     * @return {@code true}, if this environment is running in velocity mode, {@code false} otherwise
     */
    boolean isVelocity();

    /**
     * Checks if the environment is considered a spigot environment, meaning the spigot module is available.
     *
     * @return {@code true}, if this environment is running in spigot mode, {@code false} otherwise
     */
    boolean isSpigot();

    /**
     * Checks if the environment is considered a fabric environment.
     *
     * @return {@code true}, if this environment is running in fabric mode, {@code false} otherwise
     */
    boolean isFabric();

    /**
     * Checks if the environment is considered a server environment.
     *
     * @return {@code true}, if this environment is considered a server environment, {@code false} otherwise
     */
    boolean isServer();

    /**
     * Checks if the environment is considered a client environment.
     *
     * @return {@code true}, if this environment is considered a client environment, {@code false} otherwise
     */
    boolean isClient();

}
