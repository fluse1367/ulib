package eu.software4you.ulib.impl.spigot.enchantment;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

class NMSEnchantHelper {
    static List<NMSEnchantmentData> getEnchantmentDatas(int modifiedLevel, ItemStack stack, boolean allowTreasure) {
        List<NMSEnchantmentData> list = new ArrayList<>();

        boolean flag = stack.getType() == Material.BOOK;

        for (Enchantment enchantment : CustomEnchantmentHandler.getObtainableVillagerEnchants()) {
            if ((!enchantment.isTreasure() || allowTreasure) && (enchantment.canEnchantItem(stack) || flag)) {
                for (int level = enchantment.getMaxLevel(); level > enchantment.getStartLevel() - 1; --level) {
                    if (modifiedLevel >= getMinEnchantability(level) && modifiedLevel <= getMaxEnchantability(level)) {
                        list.add(new NMSEnchantmentData(enchantment, level));
                        break;
                    }
                }
            }
        }

        return list;
    }

    static int getMinEnchantability(int enchantmentLevel) {
        return 1 + enchantmentLevel * 10;
    }

    static int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 5;
    }
}
