package eu.software4you.ulib.spigot.impl.enchantment;

import eu.software4you.ulib.core.impl.Tasks;
import eu.software4you.ulib.core.reflect.Param;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Conversions;
import eu.software4you.ulib.spigot.enchantment.*;
import eu.software4you.ulib.spigot.impl.PluginSubst;
import eu.software4you.ulib.spigot.mappings.Mappings;
import lombok.SneakyThrows;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.*;

public final class EnchantUtilImpl {
    private static final Set<CustomEnchantment> customEnchantments = new HashSet<>();
    public static final Set<CustomEnchantment> READONLY_ENCHANTS = Collections.unmodifiableSet(customEnchantments);
    // method names for reflective access
    private static String methodName_enchantment_getRarity, methodName_item_getEnchantmentValue /*enchantability*/;

    static {
        // Use mappings API to get Enchantment#getRarity() and Item#getEnchantmentValue()
        Tasks.run(() -> {
            var mapping = Mappings.getMixedMapping();
            methodName_enchantment_getRarity = mapping
                    .fromSource("net.minecraft.world.item.enchantment.Enchantment").orElseThrow()
                    .methodFromSource("getRarity").orElseThrow().mappedName();
            methodName_item_getEnchantmentValue = mapping
                    .fromSource("net.minecraft.world.item.Item").orElseThrow()
                    .methodFromSource("getEnchantmentValue").orElseThrow().mappedName();
        });
    }

    public static void updateCustomEnchantmentLore(ItemStack stack) {
        ItemMeta meta = stack.getItemMeta();

        // remove all custom enchantment lines
        if (meta.hasLore()) {
            List<String> lore = EnchantUtil.filterLore(meta.getLore());
            meta.setLore(lore.isEmpty() ? null : lore);
        }

        EnchantUtil.getItemEnchants(stack).forEach((enc, level) -> {

            if (!(enc instanceof CustomEnchantment ce))
                return; // not a custom enchantment

            if (!customEnchantments.contains(ce))
                return; // not registered

            String loreLine = ce.getLoreLine();
            if (loreLine == null)
                return; // custom enchantment doesn't have a lore line

            List<String> lore = new ArrayList<>(Collections.singletonList(
                    String.format("§%s%s%s%s",
                            ce.isCursed() ? "c" : "7",
                            loreLine,
                            // append roman level number if 1 < level < 11 otherwise use the decimal number
                            (level > 1) ? level <= 10 ? " " + Conversions.toRoman(level) : level : "",
                            // append control characters
                            EnchantUtil.ENCHANTMENT_LORE_CONTROL_CHARS
                    )));
            if (meta.hasLore())
                lore.addAll(EnchantUtil.filterLore(meta.getLore()));
            meta.setLore(lore);

        });

        stack.setItemMeta(meta);
    }

    public static void setRepairCost(AnvilInventory inv, int lvl) {
        PluginSubst.getInstance().sync(() -> inv.setRepairCost(lvl));
    }

    public static int combineEnchantmentsSafe(ItemStack targetStack, ItemMeta target, ItemMeta sacrifice) {
        boolean targetIsBook = target instanceof EnchantmentStorageMeta;

        Function<Enchantment, Integer> lvlGet = targetIsBook ?
                ((EnchantmentStorageMeta) target)::getStoredEnchantLevel : target::getEnchantLevel;


        BiConsumer<Enchantment, Integer> enchAdd = targetIsBook ?
                (e, l) -> ((EnchantmentStorageMeta) target).addStoredEnchant(e, l, false)
                : (e, l) -> target.addEnchant(e, l, false);

        AtomicInteger cost = new AtomicInteger(0);
        EnchantUtil.getItemEnchants(sacrifice).forEach((ench, lvl) -> {

            if (!targetIsBook) {
                // skip enchantment to be added if enchantment cannot be applied to target
                if (!ench.canEnchantItem(targetStack))
                    return;
                // skip enchantment to be added if enchantment is conflicting with any enchantment on the target
                if (EnchantUtil.getItemEnchants(target).keySet().stream().anyMatch(ench::conflictsWith))
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

    public static boolean registerCustomEnchantment(CustomEnchantment enchantment) {
        if (!customEnchantments.add(enchantment))
            return false;

        // register enchantmenthandler if not registered
        if (CustomEnchantmentHandler.handle == null) {
            CustomEnchantmentHandler.register();
        }

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

    public static boolean unregisterCustomEnchantment(CustomEnchantment enchantment) {
        if (!customEnchantments.remove(enchantment))
            return false;

        return byKeyName((byKey, byName) -> byKey.remove(enchantment.getKey(), enchantment) && byName.remove(enchantment.getName(), enchantment));
    }

    @SneakyThrows
    public static int getItemEnchantability(ItemStack stack) {
        return ReflectUtil.scall(Integer.class, Class.forName("org.bukkit.craftbukkit.inventory.CraftItemStack"),
                "asNMSCopy().getItem().%s()".formatted(methodName_item_getEnchantmentValue),
                Param.single(ItemStack.class, stack)).orElseThrow();
    }

    private static boolean byKeyName(BiFunction<Map<NamespacedKey, Enchantment>, Map<String, Enchantment>, Boolean> fun) {
        Map<NamespacedKey, Enchantment> byKey = ReflectUtil.scall(Map.class, Enchantment.class, "byKey")
                .map(map -> Conversions.safecast(NamespacedKey.class, Enchantment.class, map).orElse(null))
                .orElseThrow();
        Map<String, Enchantment> byName = ReflectUtil.scall(Map.class, Enchantment.class, "byName")
                .map(map -> Conversions.safecast(String.class, Enchantment.class, map).orElse(null))
                .orElseThrow();

        return fun.apply(byKey, byName);
    }

    @SneakyThrows
    public static EnchantmentRarity getEnchantRarity(Enchantment enchantment) {
        if (enchantment instanceof CustomEnchantment ce)
            return ce.getEnchantmentRarity();

        String rarityName;
        var ce = Class.forName("org.bukkit.craftbukkit.enchantments.CraftEnchantment");
        if (!ce.isInstance(enchantment))
            throw new IllegalArgumentException("%s not an instance of %s".formatted(enchantment, ce));
        rarityName = ReflectUtil.scall(String.class, ce, "getRaw().%s().name()".formatted(methodName_enchantment_getRarity),
                Param.single(Enchantment.class, enchantment)).orElseThrow();

        return EnchantmentRarity.valueOf(rarityName);
    }
}
