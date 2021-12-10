package eu.software4you.spigot.multiversion;


import eu.software4you.ulib.core.api.utils.ClassUtils;

import java.util.HashMap;
import java.util.Map;

public class MultiversionManager {
    private static final Protocol version;
    private static final Map<String, Protocol> byVerStr = new HashMap<>();

    static {
        Protocol ver;
        try {
            ver = Protocol.valueOf(BukkitReflectionUtils.PackageType.getServerVersion());
        } catch (Throwable e) {
            ver = Protocol.UNKNOWN;
        }
        version = ver;
        for (Protocol prot : Protocol.values()) {
            for (String verStr : prot.versions) {
                byVerStr.put(verStr, prot);
            }
        }
    }

    public static Protocol getVersion() {
        return version;
    }

    public static Protocol getVersion(String version) {
        return byVerStr.getOrDefault(version, Protocol.UNKNOWN);
    }

    public static String netMinecraftServerPrefix() {
        return "net.minecraft.server." + version.name() + ".";
    }

    public static Class<?> netMinecraftServer(String ex) {
        return ClassUtils.forName(netMinecraftServerPrefix() + ex);
    }

    public static String orgBukkitCraftbukkitPrefix() {
        return "org.bukkit.craftbukkit." + version.name() + ".";
    }

    public static Class<?> orgBukkitCraftbukkit(String ex) {
        return ClassUtils.forName(orgBukkitCraftbukkitPrefix() + ex);
    }
}
