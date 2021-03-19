package eu.software4you.ulib.impl.spigot.enchantment;

import eu.software4you.math.MathUtils;
import eu.software4you.reflect.Parameter;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.spigot.enchantment.CustomEnchantment;
import eu.software4you.spigot.enchantment.EnchantUtil;
import eu.software4you.spigot.enchantment.EnchantmentRarity;
import eu.software4you.spigot.multiversion.BukkitReflectionUtils;
import eu.software4you.ulib.ULibSpigotPlugin;
import eu.software4you.ulib.inject.Impl;
import lombok.SneakyThrows;
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

@Impl(EnchantUtil.class)
final class EnchantUtilImpl extends EnchantUtil {
    private static final Set<CustomEnchantment> customEnchantments = new HashSet<>();

    @Override
    protected Set<CustomEnchantment> getCustomEnchantments0() {
        return Collections.unmodifiableSet(customEnchantments);
    }

    @Override
    protected void updateCustomEnchantmentLore0(ItemStack stack) {
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

            String loreLine = ce.getLoreLine();
            if (loreLine == null)
                return; // custom enchantment doesn't have a lore line

            List<String> lore = new ArrayList<>(Collections.singletonList(
                    String.format("§%s%s%s%s",
                            ce.isCursed() ? "c" : "7",
                            loreLine,
                            // append roman level number if 1 < level < 11 otherwise use the decimal number
                            (level > 1) ? level <= 10 ? " " + MathUtils.decToRoman(level) : level : "",
                            // append control characters
                            ENCHANTMENT_LORE_CONTROL_CHARS
                    )));
            if (meta.hasLore())
                lore.addAll(filterLore(meta.getLore()));
            meta.setLore(lore);

        });

        stack.setItemMeta(meta);
    }

    @Override
    protected void setRepairCost0(AnvilInventory inv, int lvl) {
        ULibSpigotPlugin.getInstance().sync(() -> inv.setRepairCost(lvl));
    }

    @Override
    protected int combineEnchantmentsSafe0(ItemStack targetStack, ItemMeta target, ItemMeta sacrifice) {
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

    @Override
    protected boolean registerCustomEnchantment0(CustomEnchantment enchantment) {
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

    @Override
    protected boolean unregisterCustomEnchantment0(CustomEnchantment enchantment) {
        if (!customEnchantments.remove(enchantment))
            return false;

        return byKeyName((byKey, byName) -> byKey.remove(enchantment.getKey(), enchantment) && byName.remove(enchantment.getName(), enchantment));
    }

    @SneakyThrows
    @Override
    protected int getItemEnchantability0(ItemStack stack) {
        // return CraftItemStack.asNMSCopy(stack).getItem().c();
        return (int) ReflectUtil.forceCall(BukkitReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"),
                null, "asNMSCopy().getItem().c()", Parameter.single(ItemStack.class, stack));
    }

    private boolean byKeyName(BiFunction<Map<NamespacedKey, Enchantment>, Map<String, Enchantment>, Boolean> fun) {
        Map<NamespacedKey, Enchantment> byKey = (Map<NamespacedKey, Enchantment>) ReflectUtil.forceCall(Enchantment.class, null, "byKey");
        Map<String, Enchantment> byName = (Map<String, Enchantment>) ReflectUtil.forceCall(Enchantment.class, null, "byName");

        return fun.apply(byKey, byName);
    }

    @SneakyThrows
    @Override
    protected EnchantmentRarity getEnchantRarity0(Enchantment enchantment) {
        String rarityName = (String) ReflectUtil.forceCall(BukkitReflectionUtils.PackageType.CRAFTBUKKIT_ENCHANTMENS.getClass("CraftEnchantment"),
                null, "getRaw().d().name()", Parameter.single(Enchantment.class, enchantment));

        return EnchantmentRarity.valueOf(rarityName);
    }
}
