package eu.software4you.ulib;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the implementation version.
 */
public enum RunMode {
    /**
     * A spigot (or fork) plugin implementation.
     */
    SPIGOT("spigot", true, false),

    /**
     * A bungeecord (or fork) plugin implementation.
     */
    BUNGEECORD("bungeecord"),

    /**
     * A velocity (or fork) plugin implementation.
     */
    VELOCITY("velocity"),

    /**
     * A standalone implementation.
     */
    STANDALONE("standalone", false, false),

    ;


    private final String name;
    private final boolean mc;
    private final boolean proxy;

    RunMode(String name) {
        this(name, true, true);
    }

    RunMode(String name, boolean mc, boolean proxy) {
        this.name = name;
        this.mc = mc;
        this.proxy = proxy;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public boolean isMinecraft() {
        return this.mc;
    }

    public boolean isProxy() {
        return mc && proxy;
    }
}
