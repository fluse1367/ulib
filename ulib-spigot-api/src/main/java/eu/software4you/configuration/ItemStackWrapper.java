package eu.software4you.ulib.core.api.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;

import java.io.File;

public class ItemStackWrapper {

    private final ConfigurationSection section;

    private ItemStack stack;

    public ItemStackWrapper(File file) {
        this(YamlConfiguration.loadConfiguration(file));
    }

    public ItemStackWrapper(ConfigurationSection section) {
        this.section = section;
    }

    public ItemStack getItemStack() {
        reload();
        return stack;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
        update();
    }

    public void reload() {
        stack = (ItemStack) ConfigurationSerialization.deserializeObject(section.getConfigurationSection("itemdata").getValues(true), ItemStack.class);
    }

    public void update() {
        section.set("itemdata", stack.serialize());
    }

    public ConfigurationSection getSection() {
        return section;
    }
}
