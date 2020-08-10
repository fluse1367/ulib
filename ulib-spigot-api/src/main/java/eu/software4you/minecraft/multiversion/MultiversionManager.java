package eu.software4you.minecraft.multiversion;


import eu.software4you.minecraft.multiversion.protocol.Protocol;
import eu.software4you.reflection.UniClass;
import eu.software4you.utils.ClassUtils;

public class MultiversionManager {
    private static final Protocol version;
    private static MultiversionManager instance = new MultiversionManager();

    static {
        Protocol ver;
        try {
            ver = Protocol.valueOf(BukkitReflectionUtils.PackageType.getServerVersion());
        } catch (Exception e) {
            ver = Protocol.UNKNOWN;
        }
        version = ver;
    }

    public static MultiversionManager getInstance() {
        return instance;
    }

    public static Protocol getVersion() {
        return version;
    }

    public static String netMinecraftServerPrefix() {
        return "net.minecraft.server." + version.name() + ".";
    }

    public static Class<?> netMinecraftServer(String ex) {
        return ClassUtils.forName(netMinecraftServerPrefix() + ex);
    }

    public static UniClass netMinecraftServerUni(String ex) {
        return new UniClass(netMinecraftServer(ex));
    }

    public static String orgBukkitCraftbukkitPrefix() {
        return "org.bukkit.craftbukkit." + version.name() + ".";
    }

    public static Class<?> orgBukkitCraftbukkit(String ex) {
        return ClassUtils.forName(orgBukkitCraftbukkitPrefix() + ex);
    }

    public static UniClass orgBukkitCraftbukkitUni(String ex) {
        return new UniClass(orgBukkitCraftbukkit(ex));
    }
}
