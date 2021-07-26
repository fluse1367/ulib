package eu.software4you.ulib.impl.spigot.enchantment;

import eu.software4you.spigot.enchantment.CustomEnchantment;
import eu.software4you.spigot.enchantment.EnchantUtil;
import org.bukkit.enchantments.Enchantment;

class NMSEnchantmentData extends NMSWeightedRandom.Weightable {
    final Enchantment enchantment;
    final int enchantmentLevel;

    NMSEnchantmentData(Enchantment en, int enchLevel) {
        super((en instanceof CustomEnchantment ? ((CustomEnchantment) en).getEnchantmentRarity() : EnchantUtil.getEnchantRarity(en)).getWeight());
        this.enchantment = en;
        this.enchantmentLevel = enchLevel;
    }
}
