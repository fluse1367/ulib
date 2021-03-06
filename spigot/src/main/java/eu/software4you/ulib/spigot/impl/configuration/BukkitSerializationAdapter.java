package eu.software4you.ulib.spigot.impl.configuration;

import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Map;

public class BukkitSerializationAdapter extends SerializationAdapters.Adapter<ConfigurationSerializable> {
    @Override
    protected ConfigurationSerializable deserialize(Class<? extends ConfigurationSerializable> clazz, Map<String, Object> elements) {
        return ConfigurationSerialization.deserializeObject(elements, clazz);
    }

    @Override
    protected Map<String, Object> serialize(ConfigurationSerializable object) {
        return object.serialize();
    }
}
