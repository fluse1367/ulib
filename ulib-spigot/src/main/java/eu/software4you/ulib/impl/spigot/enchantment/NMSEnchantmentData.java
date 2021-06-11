package eu.software4you.ulib.impl.spigot.enchantment;

import org.bukkit.enchantments.Enchantment;

class NMSEnchantmentData extends NMSWeightedRandom.Weightable {
    final Enchantment enchantment;
    final int enchantmentLevel;

    NMSEnchantmentData(Enchantment en, int enchLevel) {
        super(en.getRarity().getWeight());
        this.enchantment = en;
        this.enchantmentLevel = enchLevel;
    }
}
