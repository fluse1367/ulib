package eu.software4you.spigot.enchantment;

import org.bukkit.enchantments.Enchantment;

class NMSEnchantmentData extends NMSWeightedRandom.Weightable {
    /**
     * Enchantment object associated with this EnchantmentData
     */
    final Enchantment enchantment;

    /**
     * Enchantment level associated with this EnchantmentData
     */
    final int enchantmentLevel;

    NMSEnchantmentData(Enchantment en, int enchLevel) {
        super((en instanceof CustomEnchantment ? ((CustomEnchantment) en).getRarity() : EnchantUtil.getEnchantRarity(en)).getWeight());
        this.enchantment = en;
        this.enchantmentLevel = enchLevel;
    }
}
