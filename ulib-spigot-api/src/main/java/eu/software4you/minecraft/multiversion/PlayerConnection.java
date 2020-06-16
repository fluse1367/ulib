package eu.software4you.minecraft.multiversion;

import eu.software4you.minecraft.multiversion.packet.Packet;
import eu.software4you.reflection.UniClass;
import org.bukkit.Location;

public class PlayerConnection extends UniClass {
    private final Object playerConnection;

    public PlayerConnection(Object playerConnection) {
        super(MultiversionManager.netMinecraftServer("PlayerConnection"));
        this.playerConnection = playerConnection;
    }

    public void sendPacket(Packet packet) {
        sendPacket(packet.invoke());
    }

    public void sendPacket(Object packet) {
        getMethod("sendPacket", getClazz()).invoke(playerConnection, packet);
    }

    public void teleport(Location dest) {
        getMethod("teleport", getClazz()).invoke(playerConnection, dest);
    }
}
