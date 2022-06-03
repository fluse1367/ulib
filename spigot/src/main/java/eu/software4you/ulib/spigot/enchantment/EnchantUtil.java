package eu.software4you.ulib.spigot.enchantment;

import eu.software4you.ulib.spigot.impl.enchantment.EnchantUtilImpl;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.Collectors;

public final class EnchantUtil {
    /**
     * Control characters to determine custom enchantment lores. They will be appended to any custom enchantment lore line.
     */
    public static final String ENCHANTMENT_LORE_CONTROL_CHARS = "§3§o §r ";

    /**
     * Returns an immutable copy of the currently registered custom enchantments.
     *
     * @return an immutable copy of the currently registered custom enchantments
     */
    @NotNull
    @UnmodifiableView
    public static Set<CustomEnchantment> getCustomEnchantments() {
        return EnchantUtilImpl.READONLY_ENCHANTS;
    }

    /**
     * Returns the enchantments an item stack contains.
     *
     * @param stack the item stack to fetch the enchantments from.
     * @return the enchantments
     * @see #getItemEnchants(ItemMeta)
     */
    @NotNull
    @Unmodifiable
    public static Map<Enchantment, Integer> getItemEnchants(@NotNull ItemStack stack) {
        return getItemEnchants(stack.getItemMeta());
    }

    /**
     * Returns the enchantments an item meta contains.
     *
     * @param meta the item meta to fetch the enchantments from.
     * @return the enchantments
     * @see #getItemEnchants(ItemStack)
     */
    @NotNull
    @Unmodifiable
    public static Map<Enchantment, Integer> getItemEnchants(@NotNull ItemMeta meta) {
        if (meta instanceof EnchantmentStorageMeta)
            return ((EnchantmentStorageMeta) meta).getStoredEnchants();
        return meta.getEnchants();
    }

    /**
     * Returns the custom enchantments an item stack contains.
     *
     * @param stack the item stack to fetch the custom enchantments from.
     * @return the custom enchantments
     * @see #getCustomItemEnchants(ItemMeta)
     */
    @NotNull
    @Unmodifiable
    public static Map<CustomEnchantment, Integer> getCustomItemEnchants(@NotNull ItemStack stack) {
        return getCustomItemEnchants(Objects.requireNonNull(stack.getItemMeta()));
    }

    /**
     * Returns the custom enchantments an item meta contains.
     *
     * @param meta the item meta to fetch the custom enchantments from.
     * @return the custom enchantments
     * @see #getCustomItemEnchants(ItemStack)
     */
    @NotNull
    @Unmodifiable
    public static Map<CustomEnchantment, Integer> getCustomItemEnchants(@NotNull ItemMeta meta) {
        Map<Enchantment, Integer> enchants = getItemEnchants(meta);
        return enchants.keySet().stream().filter(enc -> enc instanceof CustomEnchantment)
                .collect(Collectors.toMap(enchantment -> (CustomEnchantment) enchantment, enchants::get));
    }

    /**
     * Updates an item's lore with current custom enchantment namespace key / name.
     * This also means, removal of lore lines if the item does not have the respective enchantment (done by removing lines ending with {@link #ENCHANTMENT_LORE_CONTROL_CHARS}).
     *
     * @param stack the item stack to be updated
     * @see Enchantment#getKey()
     * @see NamespacedKey#getKey()
     * @see Enchantment#getName()
     */
    public static void updateCustomEnchantmentLore(@NotNull ItemStack stack) {
        EnchantUtilImpl.updateCustomEnchantmentLore(stack);
    }

    /**
     * Removes the enchantment lore lines from a list.
     * This is done by removing lines ending with {@link #ENCHANTMENT_LORE_CONTROL_CHARS}.
     *
     * @param lore the list
     * @return the list without the custom enchantment lore lines
     */
    @NotNull
    public static List<String> filterLore(@NotNull List<String> lore) {
        return lore.stream()
                .filter(line -> !line.endsWith(ENCHANTMENT_LORE_CONTROL_CHARS))
                .collect(Collectors.toList());
    }

    /**
     * Combines enchantments from two item metas.
     *
     * @param targetStack the target item stack, used for compatibility checking
     * @param target      the meta the enchantments should be added to, it's instance will be modified
     * @param sacrifice   the meta the enchantments should be taken from
     * @return the enchanting exp cost
     * @see <a href="https://minecraft.gamepedia.com/Anvil_mechanics#Combining_items">https://minecraft.gamepedia.com/Anvil_mechanics#Combining_items</a>
     * @see <a href="https://minecraft.gamepedia.com/Anvil_mechanics#Costs_for_combining_enchantments">https://minecraft.gamepedia.com/Anvil_mechanics#Costs_for_combining_enchantments</a>
     */
    public static int combineEnchantmentsSafe(@NotNull ItemStack targetStack, @NotNull ItemMeta target, @NotNull ItemMeta sacrifice) {
        return EnchantUtilImpl.combineEnchantmentsSafe(targetStack, target, sacrifice);
    }

    /**
     * Registers a custom enchantment into bukkit.
     * <p>
     * <b>Warning:</b> Registering a custom enchantment will download the current server mappings
     * (they're needed for some reflection operations) and store them into memory.
     * This will, if not already happened, take a couple of seconds and also occupy a lot of memory.
     *
     * @param enchantment the enchantment to register
     * @return if the registration was successful
     */
    public static boolean registerCustomEnchantment(@NotNull CustomEnchantment enchantment) {
        return EnchantUtilImpl.registerCustomEnchantment(enchantment);
    }

    /**
     * Removes a custom enchantment from bukkit's registration.
     *
     * @param enchantment the enchantment to unregister
     * @return if the removal was successful
     */
    public static boolean unregisterCustomEnchantment(@NotNull CustomEnchantment enchantment) {
        return EnchantUtilImpl.unregisterCustomEnchantment(enchantment);
    }

    public static int getItemEnchantability(@NotNull ItemStack stack) {
        return EnchantUtilImpl.getItemEnchantability(stack);
    }

    /**
     * Retrieves the enchantment rarity of an enchantment.
     *
     * @param enchantment the enchantment
     * @return the rarity
     */
    @NotNull
    public static EnchantmentRarity getEnchantRarity(@NotNull Enchantment enchantment) {
        return EnchantUtilImpl.getEnchantRarity(enchantment);
    }
}
