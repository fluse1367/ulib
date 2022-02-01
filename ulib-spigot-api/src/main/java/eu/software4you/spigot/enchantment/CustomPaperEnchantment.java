package eu.software4you.spigot.enchantment;

import io.papermc.paper.enchantments.EnchantmentRarity;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.EnchantmentTarget;
import org.jetbrains.annotations.NotNull;

/**
 * Can be used on paper to create a custom enchantment.
 */
public abstract class CustomPaperEnchantment extends CustomEnchantment {

    public CustomPaperEnchantment(@NotNull NamespacedKey key, boolean treasure, boolean cursed, boolean obtainableViaVillagerTrading, int startLevel, int maxLevel, EnchantmentTarget itemTarget, eu.software4you.spigot.enchantment.EnchantmentRarity rarity) {
        super(key, treasure, cursed, obtainableViaVillagerTrading, startLevel, maxLevel, itemTarget, rarity);
    }

    @Override
    public @NotNull EnchantmentRarity getRarity() {
        return EnchantmentRarity.valueOf(getEnchantmentRarity().name());
    }
}
