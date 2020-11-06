package eu.software4you.minecraft.enchantment;

import eu.software4you.math.MathUtils;
import eu.software4you.minecraft.multiversion.BukkitReflectionUtils.PackageType;
import eu.software4you.minecraft.plugin.ExtendedPlugin;
import eu.software4you.reflection.ReflectUtil;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class EnchantUtil {
    /**
     * Control characters to determine custom enchantment lores. They will be appended to any custom enchantment lore line.
     */
    public static final String ENCHANTMENT_LORE_CONTROL_CHARS = "§3§o §r ";
    private static final Set<CustomEnchantment> customEnchantments = new HashSet<>();

    /**
     * Returns an immutable copy of the currently registered custom enchantments.
     *
     * @return an immutable copy of the currently registered custom enchantments
     */
    public static Set<CustomEnchantment> getCustomEnchantments() {
        return Collections.unmodifiableSet(customEnchantments);
    }

    /**
     * Returns the enchantments an item stack contains.
     *
     * @param stack the item stack to fetch the enchantments from.
     * @return the enchantments
     * @see #getItemEnchants(ItemMeta)
     */
    public static Map<Enchantment, Integer> getItemEnchants(ItemStack stack) {
        return getItemEnchants(stack.getItemMeta());
    }

    /**
     * Returns the enchantments an item meta contains.
     *
     * @param meta the item meta to fetch the enchantments from.
     * @return the enchantments
     * @see #getItemEnchants(ItemStack)
     */
    public static Map<Enchantment, Integer> getItemEnchants(ItemMeta meta) {
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
    public static Map<CustomEnchantment, Integer> getCustomItemEnchants(ItemStack stack) {
        return getCustomItemEnchants(stack.getItemMeta());
    }

    /**
     * Returns the custom enchantments an item meta contains.
     *
     * @param meta the item meta to fetch the custom enchantments from.
     * @return the custom enchantments
     * @see #getCustomItemEnchants(ItemStack)
     */
    public static Map<CustomEnchantment, Integer> getCustomItemEnchants(ItemMeta meta) {
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
    public static void updateCustomEnchantmentLore(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        // remove all custom enchantment lines
        if (meta.hasLore()) {
            List<String> lore = filterLore(meta.getLore());
            meta.setLore(lore.isEmpty() ? null : lore);
        }

        getItemEnchants(stack).forEach((enc, level) -> {

            if (!(enc instanceof CustomEnchantment))
                return; // not a custom enchantment

            CustomEnchantment ce = (CustomEnchantment) enc;
            if (!customEnchantments.contains(ce))
                return; // not registered

            List<String> lore = new ArrayList<>(Collections.singletonList(
                    String.format("§%s%s%s%s",
                            ce.isCursed() ? "c" : "7",
                            ce.getLoreLine(),
                            // append roman level number if 1 < level < 11 otherwise use the decimal number
                            (level > 1) ? level <= 10 ? " " + MathUtils.decToRoman(level) : level : "",
                            // append control characters
                            ENCHANTMENT_LORE_CONTROL_CHARS
                    )));
            if (meta.hasLore())
                lore.addAll(filterLore(meta.getLore()));
            meta.setLore(lore);
            stack.setItemMeta(meta);

        });
    }

    /**
     * Removes the enchantment lore lines from a list.
     * This is done by removing lines ending with {@link #ENCHANTMENT_LORE_CONTROL_CHARS}.
     *
     * @param lore the list
     * @return the list without the custom enchantment lore lines
     */
    private static List<String> filterLore(List<String> lore) {
        return lore.stream().filter(line -> !line.endsWith(ENCHANTMENT_LORE_CONTROL_CHARS)).collect(Collectors.toList());
    }

    public static void setRepairCost(AnvilInventory inv, int lvl) {
        ((ExtendedPlugin) Bukkit.getPluginManager().getPlugin("uLib")).sync(() -> inv.setRepairCost(lvl));
    }

    /**
     * Combines enchantments from two item metas.
     * <p>
     * See {@literal https://minecraft.gamepedia.com/Anvil_mechanics#Combining_items}
     * <p>
     * See {@literal https://minecraft.gamepedia.com/Anvil_mechanics#Costs_for_combining_enchantments}
     *
     * @param targetStack the target item stack, used for compatibility checking
     * @param target      the meta the enchantments should be added to, it's instance will be modified
     * @param sacrifice   the meta the enchantments should be taken from
     * @return the enchanting exp cost
     */
    public static int combineEnchantmentsSafe(ItemStack targetStack, ItemMeta target, ItemMeta sacrifice) {

        boolean targetIsBook = target instanceof EnchantmentStorageMeta;

        Function<Enchantment, Integer> lvlGet = targetIsBook ?
                ((EnchantmentStorageMeta) target)::getStoredEnchantLevel : target::getEnchantLevel;


        BiConsumer<Enchantment, Integer> enchAdd = targetIsBook ?
                (e, l) -> ((EnchantmentStorageMeta) target).addStoredEnchant(e, l, false)
                : (e, l) -> target.addEnchant(e, l, false);

        AtomicInteger cost = new AtomicInteger(0);
        getItemEnchants(sacrifice).forEach((ench, lvl) -> {

            if (!targetIsBook) {
                // skip enchantment to be added if enchantment cannot be applied to target
                if (!ench.canEnchantItem(targetStack))
                    return;
                // skip enchantment to be added if enchantment is conflicting with any enchantment on the target
                if (getItemEnchants(target).keySet().stream().anyMatch(ench::conflictsWith))
                    return;
            }

            int currLvl = lvlGet.apply(ench);
            if (currLvl == lvl && lvl + 1 <= ench.getMaxLevel()) {
                lvl++;
            } else {
                lvl = Integer.max(currLvl, lvl);
            }

            enchAdd.accept(ench, lvl);
            cost.addAndGet(lvl);
        });

        return cost.get();
    }

    /**
     * Registers a custom enchantment into bukkit.
     *
     * @param enchantment the enchantment to register
     * @return if the registration was successful
     */
    public static boolean registerCustomEnchantment(CustomEnchantment enchantment) {
        if (!customEnchantments.add(enchantment))
            return false;

        return byKeyName((byKey, byName) -> {
            if (byKey.containsKey(enchantment.getKey()) || byName.containsKey(enchantment.getName())) {
                // throw new IllegalArgumentException("Cannot set already-set enchantment");
                return false;
            }

            byKey.put(enchantment.getKey(), enchantment);
            byName.put(enchantment.getName(), enchantment);
            return true;
        });
    }

    /**
     * Removes a custom enchantment from bukkit's registration.
     *
     * @param enchantment the enchantment to unregister
     * @return if the removal was successful
     */
    public static boolean unregisterCustomEnchantment(CustomEnchantment enchantment) {
        if (!customEnchantments.remove(enchantment))
            return false;

        return byKeyName((byKey, byName) -> byKey.remove(enchantment.getKey(), enchantment) && byName.remove(enchantment.getName(), enchantment));
    }

    @SneakyThrows
    public static int getItemEnchantability(ItemStack stack) {

        // return CraftItemStack.asNMSCopy(stack).getItem().c();

        return (int) ReflectUtil.forceCall(PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"),
                null, "asNMSCopy().getItem().c()",
                Collections.singletonList(new ReflectUtil.Parameter<>(ItemStack.class, stack)));
    }

    @SneakyThrows
    private static boolean byKeyName(BiFunction<Map<NamespacedKey, Enchantment>, Map<String, Enchantment>, Boolean> fun) {
        Map<NamespacedKey, Enchantment> byKey = (Map<NamespacedKey, Enchantment>) ReflectUtil.forceCall(Enchantment.class, null, "byKey");
        Map<String, Enchantment> byName = (Map<String, Enchantment>) ReflectUtil.forceCall(Enchantment.class, null, "byName");

        return fun.apply(byKey, byName);
    }

    @SneakyThrows
    public static EnchantmentRarity getEnchantRarity(Enchantment enchantment) {
        String rarityName = (String) ReflectUtil.forceCall(PackageType.CRAFTBUKKIT_ENCHANTMENS.getClass("CraftEnchantment"),
                null, "getRaw().d().name()", Collections.singletonList(
                        new ReflectUtil.Parameter<>(Enchantment.class, enchantment)));

        return EnchantmentRarity.valueOf(rarityName);
    }
}
