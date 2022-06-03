package eu.software4you.ulib.spigot.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public enum Protocol {
    // 1.7
    v1_7_R1(4, "1.7.2"),
    v1_7_R2(4, "1.7.5"),
    v1_7_R3(5, "1.7.8"),
    v1_7_R4(5, "1.7.9", "1.7.10"),
    // 1.8
    v1_8_R1(47, "1.8"),
    v1_8_R2(47, "1.8.3"),
    v1_8_R3(47, "1.8.4", "1.8.5", "1.8.6", "1.8.7", "1.8.8"),
    // 1.9
    v1_9_R1(109, "1.9", "1.9.2"),
    v1_9_R2(110, "1.9.4"),
    // 1.10
    v1_10_R1(210, "1.10", "1.10.2"),
    // 1.11
    v1_11_R1(316, "1.11", "1.11.1", "1.11.2"),
    // 1.12
    v1_12_R1(340, "1.12", "1.12.1", "1.12.2"),
    // 1.13
    v1_13_R1(401, "1.13"),
    v1_13_R2(404, "1.13.1", "1.13.2"),
    // 1.14
    v1_14_R1(498, "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4"),
    // 1.15
    v1_15_R1(575, "1.15", "1.15.1", "1.15.2"),
    // 1.16
    v1_16_R1(736, "1.16.1"),
    v1_16_R2(753, "1.16.2", "1.16.3"),
    v1_16_R3(754, "1.16.4", "1.16.5"),
    // 1.17
    v1_17_R1(756, "1.17", "1.17.1"),
    // 1.18
    v1_18_R1(757, "1.18", "1.18.1"),
    v1_18_R2(758, "1.18.2"),
    // unknown
    UNKNOWN(-1);

    private static final Map<String, Protocol> byVerStr = new HashMap<>();
    private static final Protocol version;

    static {
        // determine current mc version
        Protocol current;
        try {
            String mcVer = Bukkit.getServer().getClass().getPackage().getName().substring(23);
            current = valueOf(mcVer);
        } catch (Throwable t) {
            current = UNKNOWN;
        }
        version = current;

        // populate map
        for (Protocol protocol : values()) {
            for (String version : protocol.versions) {
                byVerStr.putIfAbsent(version, protocol);
            }
        }
    }

    /**
     * @return the protocol, the platform is currently implementing, or {@link #UNKNOWN} if the protocol couldn't be determined
     */
    @NotNull
    public static Protocol getPlatformProtocol() {
        return version;
    }

    /**
     * Attempts to parse a plain vanilla version string into the corresponding protocol.
     *
     * @param version a plain vanilla version string (e.g. {@code 1.8.9})
     * @return an optional wrapping the protocol on parse success, otherwise an empty optional
     */
    @NotNull
    public static Optional<Protocol> parse(@NotNull String version) {
        return byVerStr.containsKey(version) ? Optional.of(byVerStr.get(version)) : Optional.empty();
    }

    /**
     * Attempts to parse a plain vanilla version string into the corresponding protocol.
     *
     * @param version a plain vanilla version string (e.g. {@code 1.8.9})
     * @return the corresponding protocol on parse success, otherwise {@link #UNKNOWN}
     */
    @NotNull
    public static Protocol of(@NotNull String version) {
        return parse(version).orElse(UNKNOWN);
    }

    final int protocol;
    final String[] versions;

    Protocol(int protocol, String... versions) {
        this.protocol = protocol;
        this.versions = versions;
    }

    /**
     * @return the plain vanilla version string
     */
    @NotNull
    public String getVersion() {
        if (versions.length == 0)
            throw new UnsupportedOperationException("Protocol unknown");
        return versions[versions.length - 1];
    }

    /**
     * @return the int version representation of this protocol
     */
    public int asInt() {
        return protocol;
    }

    /**
     * Determines weather the current protocol is higher ("above") another protocol.
     *
     * @param toCompare the protocol to compare
     * @return {@code true} if this protocol has a higher version number than the comparing one, {@code false} otherwise
     */
    public boolean above(@NotNull Protocol toCompare) {
        return protocol > toCompare.protocol;
    }

    /**
     * Determines weather the current protocol is higher ("above") or equal to another protocol.
     *
     * @param toCompare the protocol to compare
     * @return {@code true} if this protocol has a higher or the same version number than the comparing one, {@code false} otherwise
     */
    public boolean atLeast(@NotNull Protocol toCompare) {
        return protocol >= toCompare.protocol;
    }

    /**
     * Determines weather the current protocol is lower ("below") another protocol.
     *
     * @param toCompare the protocol to compare
     * @return {@code true} if this protocol has a lower version number than the comparing one, {@code false} otherwise
     */
    public boolean below(@NotNull Protocol toCompare) {
        return protocol < toCompare.protocol;
    }

    /**
     * Determines weather the current protocol is lower ("below") or equal to another protocol.
     *
     * @param toCompare the protocol to compare
     * @return {@code true} if this protocol has a lower or the same version number than the comparing one, {@code false} otherwise
     */
    public boolean atTheMost(@NotNull Protocol toCompare) {
        return protocol <= toCompare.protocol;
    }
}
