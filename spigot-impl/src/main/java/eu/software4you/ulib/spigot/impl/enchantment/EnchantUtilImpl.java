package eu.software4you.ulib.spigot.impl.enchantment;

import eu.software4you.ulib.core.api.reflect.Parameter;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import eu.software4you.ulib.core.api.util.Conversions;
import eu.software4you.ulib.core.impl.Tasks;
import eu.software4you.ulib.spigot.api.enchantment.CustomEnchantment;
import eu.software4you.ulib.spigot.api.enchantment.EnchantUtil;
import eu.software4you.ulib.spigot.api.enchantment.EnchantmentRarity;
import eu.software4you.ulib.spigot.api.mappings.Mappings;
import eu.software4you.ulib.spigot.api.multiversion.BukkitReflectionUtils;
import eu.software4you.ulib.spigot.impl.PluginSubst;
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

final class EnchantUtilImpl extends EnchantUtil {
    private static final Set<CustomEnchantment> customEnchantments = new HashSet<>();
    private String methodName_enchantment_getRarity;
    private String methodName_item_getEnchantmentValue; // enchantability

    public EnchantUtilImpl() {
        // Use mappings API to get Enchantment#getRarity() and Item#getEnchantmentValue()
        Tasks.run(() -> {
            var mapping = Mappings.getMixedMapping();
            methodName_enchantment_getRarity = mapping
                    .fromSource("net.minecraft.world.item.enchantment.Enchantment")
                    .methodFromSource("getRarity").mappedName();
            methodName_item_getEnchantmentValue = mapping
                    .fromSource("net.minecraft.world.item.Item")
                    .methodFromSource("getEnchantmentValue").mappedName();
        });
    }

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
        PluginSubst.getInstance().sync(() -> inv.setRepairCost(lvl));
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

        // register enchantmenthandler if not registered
        if (!PluginSubst.getInstance().isListening(CustomEnchantmentHandler.class)) {
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

    @Override
    protected boolean unregisterCustomEnchantment0(CustomEnchantment enchantment) {
        if (!customEnchantments.remove(enchantment))
            return false;

        return byKeyName((byKey, byName) -> byKey.remove(enchantment.getKey(), enchantment) && byName.remove(enchantment.getName(), enchantment));
    }

    @SneakyThrows
    @Override
    protected int getItemEnchantability0(ItemStack stack) {
        return (int) ReflectUtil.forceCall(BukkitReflectionUtils.PackageType.CRAFTBUKKIT_INVENTORY.getClass("CraftItemStack"),
                null, "asNMSCopy().getItem()." + methodName_item_getEnchantmentValue + "()", Parameter.single(ItemStack.class, stack));
    }

    private boolean byKeyName(BiFunction<Map<NamespacedKey, Enchantment>, Map<String, Enchantment>, Boolean> fun) {
        Map<NamespacedKey, Enchantment> byKey = (Map<NamespacedKey, Enchantment>) ReflectUtil.forceCall(Enchantment.class, null, "byKey");
        Map<String, Enchantment> byName = (Map<String, Enchantment>) ReflectUtil.forceCall(Enchantment.class, null, "byName");

        return fun.apply(byKey, byName);
    }

    @SneakyThrows
    @Override
    protected EnchantmentRarity getEnchantRarity0(Enchantment enchantment) {
        if (enchantment instanceof CustomEnchantment ce)
            return ce.getEnchantmentRarity();

        String rarityName;
        var ce = BukkitReflectionUtils.PackageType.CRAFTBUKKIT_ENCHANTMENS.getClass("CraftEnchantment");
        if (!ce.isInstance(enchantment))
            throw new IllegalArgumentException("%s not an instance of %s".formatted(enchantment, ce));
        rarityName = (String) ReflectUtil.forceCall(ce, null, "getRaw().%s().name()".formatted(methodName_enchantment_getRarity),
                Parameter.single(Enchantment.class, enchantment));

        return EnchantmentRarity.valueOf(rarityName);
    }
}
