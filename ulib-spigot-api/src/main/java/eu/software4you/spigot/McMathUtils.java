package eu.software4you.spigot;

import eu.software4you.math.Coordinate3D;
import org.bukkit.Location;
import org.bukkit.World;

public class McMathUtils {
    public static float invertYaw(float yaw) {
        if (yaw <= 0.0F) {
            yaw += 180.0F;
        } else if (yaw > 0.0F) {
            yaw -= 180.0F;
        }

        return yaw;
    }

    public static Coordinate3D locationToCoordinate3D(Location loc) {
        return new Coordinate3D(loc.getX(), loc.getY(), loc.getZ());
    }

    public static Location coordinate3DToLocation(Coordinate3D coo, World world) {
        return new Location(world, coo.getX(), coo.getY(), coo.getZ());
    }

    public static Location coordinate3DToLocation(Coordinate3D coo, World world, float yaw, float pitch) {
        return new Location(world, coo.getX(), coo.getY(), coo.getZ(), pitch, yaw);
    }
}
