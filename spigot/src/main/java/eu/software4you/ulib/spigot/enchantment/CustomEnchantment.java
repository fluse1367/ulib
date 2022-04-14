package eu.software4you.ulib.spigot.enchantment;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a custom enchantment. Must be registered with {@link EnchantUtil#registerCustomEnchantment(CustomEnchantment)}.
 */
public abstract class CustomEnchantment extends Enchantment {

    private final boolean treasure;
    private final boolean cursed;
    private final boolean obtainableViaVillagerTrading;
    private final int startLevel;
    private final int maxLevel;
    private final EnchantmentTarget itemTarget;
    private final EnchantmentRarity rarity;

    public CustomEnchantment(@NotNull NamespacedKey key, boolean treasure, boolean cursed, boolean obtainableViaVillagerTrading, int startLevel, int maxLevel, EnchantmentTarget itemTarget, EnchantmentRarity rarity) {
        super(key);

        Validate.isTrue(startLevel > 0, "Minimum level for an enchantment is 1");
        Validate.isTrue(maxLevel >= startLevel, "Maximum level for an enchantment must not be lower then it's minimum level");

        this.treasure = treasure;
        this.cursed = cursed;
        this.obtainableViaVillagerTrading = obtainableViaVillagerTrading;
        this.startLevel = startLevel;
        this.maxLevel = maxLevel;
        this.itemTarget = itemTarget;
        this.rarity = rarity;
    }

    /**
     * Gets the unique name of this enchantment
     *
     * @return Unique name
     */
    @Override
    public @NotNull String getName() {
        return getKey().getKey().toUpperCase();
    }

    /**
     * Checks if this enchantment is a treasure enchantment.
     * <br>
     * Treasure enchantments can only be received via looting, trading, or
     * fishing.
     *
     * @return true if the enchantment is a treasure enchantment
     */
    @Override
    public boolean isTreasure() {
        return treasure;
    }

    /**
     * Returns if the enchantment is a cursed enchantment.
     * If so, the enchantment cannot be removed with a grindstone
     * and it also cannot be obtained with an enchanting table.
     *
     * @return if the enchantment is a cursed enchantment
     */
    @Override
    public boolean isCursed() {
        return cursed;
    }

    /**
     * Returns if the enchantment can be obtained via trading with a librarian villager.
     *
     * @return if the enchantment can be obtained via trading with a librarian
     */
    public boolean isObtainableViaVillagerTrading() {
        return obtainableViaVillagerTrading;
    }

    /**
     * Gets the level that this Enchantment should start at
     *
     * @return Starting level of the Enchantment
     */
    @Override
    public int getStartLevel() {
        return startLevel;
    }

    /**
     * Gets the maximum level that this Enchantment may become.
     *
     * @return Maximum level of the Enchantment
     */
    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * Gets the type of {@link ItemStack} that may fit this Enchantment.
     *
     * @return Target type of the Enchantment
     */
    @NotNull
    @Override
    public EnchantmentTarget getItemTarget() {
        return itemTarget;
    }

    public EnchantmentRarity getEnchantmentRarity() {
        return rarity;
    }

    /**
     * Returns the enchantment name, displayed in the lore.
     *
     * @return the enchantment name, displayed in the lore.
     */
    public String getLoreLine() {
        return WordUtils.capitalize(getKey().getKey().toLowerCase().replace("_", " "));
    }

    /**
     * Registers this enchantment.
     *
     * @return if the registration was successful
     */
    public boolean register() {
        return EnchantUtil.registerCustomEnchantment(this);
    }

    /**
     * Removes this enchantment's registration.
     *
     * @return if the removal was successful
     */
    public boolean unregister() {
        return EnchantUtil.unregisterCustomEnchantment(this);
    }
}
