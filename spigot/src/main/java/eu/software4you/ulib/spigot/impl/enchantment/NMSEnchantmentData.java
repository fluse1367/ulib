package eu.software4you.ulib.spigot.impl.enchantment;

import eu.software4you.ulib.spigot.enchantment.EnchantUtil;
import org.bukkit.enchantments.Enchantment;

class NMSEnchantmentData extends NMSWeightedRandom.Weightable {
    final Enchantment enchantment;
    final int enchantmentLevel;

    NMSEnchantmentData(Enchantment en, int enchLevel) {
        super(EnchantUtil.getEnchantRarity(en).getWeight());
        this.enchantment = en;
        this.enchantmentLevel = enchLevel;
    }
}
