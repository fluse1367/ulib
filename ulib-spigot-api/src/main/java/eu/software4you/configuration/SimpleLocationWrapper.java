package eu.software4you.configuration;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ported.org.bukkit.configuration.ConfigurationSection;
import ported.org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class SimpleLocationWrapper {

    private final ConfigurationSection section;

    private Location location;

    public SimpleLocationWrapper(SimpleConfigurationWrapper wrapper) {
        this(wrapper.section());
    }

    public SimpleLocationWrapper(File file) {
        this(YamlConfiguration.loadConfiguration(file));
    }

    public SimpleLocationWrapper(ConfigurationSection section) {
        this.section = section;
    }

    public Location getLocation() {
        reload();
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
        update();
    }

    public void reload() {
        location = new Location(Bukkit.getWorld(section.getString("world")),
                section.getDouble("x"), section.getDouble("y"), section.getDouble("z"),
                (float) section.getDouble("yaw"), (float) section.getDouble("pitch"));
    }

    public void update() {
        section.set("world", location.getWorld().getName());
        section.set("x", location.getX());
        section.set("y", location.getY());
        section.set("z", location.getZ());
        section.set("yaw", location.getYaw());
        section.set("pitch", location.getPitch());
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
