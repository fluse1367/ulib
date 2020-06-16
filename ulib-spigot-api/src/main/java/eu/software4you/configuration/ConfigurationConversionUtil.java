package eu.software4you.configuration;


import ported.org.bukkit.configuration.ConfigurationSection;
import ported.org.bukkit.configuration.file.YamlConfiguration;

public class ConfigurationConversionUtil {
    public static YamlConfiguration toPorted(org.bukkit.configuration.ConfigurationSection section) {
        YamlConfiguration yaml = new YamlConfiguration();
        section.getValues(true).forEach(yaml::set);
        return yaml;
    }

    public static org.bukkit.configuration.file.YamlConfiguration toBukkit(ConfigurationSection section) {
        org.bukkit.configuration.file.YamlConfiguration yaml = new org.bukkit.configuration.file.YamlConfiguration();
        section.getValues(true).forEach(yaml::set);
        return yaml;
    }
}
