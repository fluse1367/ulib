package eu.software4you.spigot.scheme;

import org.bukkit.Material;

public class BlockData {
    private final Material material;
    private final short durability;

    BlockData(Material material, short durability) {
        this.material = material;
        this.durability = durability;
    }

    public Material getMaterial() {
        return material;
    }

    public short getDurability() {
        return durability;
    }
}
