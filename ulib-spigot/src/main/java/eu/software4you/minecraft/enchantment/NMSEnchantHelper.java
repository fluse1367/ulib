package eu.software4you.minecraft.enchantment;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class NMSEnchantHelper {
    static List<NMSEnchantmentData> buildEnchantmentList(Random randomIn, ItemStack stack, int p_77513_2_, boolean allowTreasure) {
        List<NMSEnchantmentData> returnList = new ArrayList<>();

        int enchantability = EnchantUtil.getItemEnchantability(stack);

        if (enchantability <= 0) {
            return returnList;
        } else {
            p_77513_2_ = p_77513_2_ + 1 + randomIn.nextInt(enchantability / 4 + 1) + randomIn.nextInt(enchantability / 4 + 1);
            float f = (randomIn.nextFloat() + randomIn.nextFloat() - 1.0F) * 0.15F;
            p_77513_2_ = NMSMathHelper.clamp(Math.round((float) p_77513_2_ + (float) p_77513_2_ * f), 1, Integer.MAX_VALUE);

            List<NMSEnchantmentData> list1 = getEnchantmentDatas(p_77513_2_, stack, allowTreasure);

            if (!list1.isEmpty()) {
                returnList.add(NMSWeightedRandom.getRandomItem(randomIn, list1));

                while (randomIn.nextInt(50) <= p_77513_2_) {
                    removeIncompatible(list1, returnList.get(returnList.size() - 1));

                    if (list1.isEmpty()) {
                        break;
                    }

                    returnList.add(NMSWeightedRandom.getRandomItem(randomIn, list1));
                    p_77513_2_ /= 2;
                }
            }

            return returnList;
        }
    }

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

    static void removeIncompatible(List<NMSEnchantmentData> p_185282_0_, NMSEnchantmentData p_185282_1_) {

        p_185282_0_.removeIf(nmsEnchantmentData -> p_185282_1_.enchantment.conflictsWith((nmsEnchantmentData).enchantment));
    }

    /**
     * Returns the enchantability of itemstack, using a separate calculation for each enchantNum (0, 1 or 2), cutting to
     * the max enchantability power of the table, which is locked to a max of 15.
     */
    static int calcItemStackEnchantability(Random rand, int enchantNum, int power, ItemStack stack) {
        int enchantability = EnchantUtil.getItemEnchantability(stack);

        if (enchantability <= 0) {
            return 0;
        } else {
            if (power > 15) {
                power = 15;
            }

            int j = rand.nextInt(8) + 1 + (power >> 1) + rand.nextInt(power + 1);

            if (enchantNum == 0) {
                return Math.max(j / 3, 1);
            } else {
                return enchantNum == 1 ? j * 2 / 3 + 1 : Math.max(j, power * 2);
            }
        }
    }
}
