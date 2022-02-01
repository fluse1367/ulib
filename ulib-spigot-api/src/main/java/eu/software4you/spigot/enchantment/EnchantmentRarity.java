package eu.software4you.spigot.enchantment;

public enum EnchantmentRarity {
    COMMON(10),
    UNCOMMON(5),
    RARE(2),
    VERY_RARE(1);

    private final int weight;

    EnchantmentRarity(int rarityWeight) {
        this.weight = rarityWeight;
    }

    public int getWeight() {
        return this.weight;
    }
}
