package eu.software4you.ulib.impl.spigot.enchantment;

import com.cryptomorin.xseries.XEnchantment;
import com.destroystokyo.paper.event.inventory.PrepareResultEvent;
import eu.software4you.reflect.ReflectUtil;
import eu.software4you.spigot.enchantment.CustomEnchantment;
import eu.software4you.spigot.enchantment.EnchantUtil;
import eu.software4you.spigot.mappings.Mappings;
import eu.software4you.spigot.multiversion.BukkitReflectionUtils.PackageType;
import eu.software4you.ulib.ULibSpigotPlugin;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class CustomEnchantmentHandler implements Listener {
    private final String getEnchantmentSeedMethodName;
    private final ULibSpigotPlugin pl;

    private CustomEnchantmentHandler(ULibSpigotPlugin pl) {
        this.pl = pl;
        // Use Mappings API to get xpSeed
        getEnchantmentSeedMethodName = Mappings.getVanillaMapping()
                .get("net.minecraft.world.entity.player.Player")
                .getMethod("getEnchantmentSeed").getObfuscatedName();
    }

    static void register() {
        val pl = ULibSpigotPlugin.getInstance();
        val handler = new CustomEnchantmentHandler(pl);

        pl.registerEvents(handler);
        pl.registerEvents(ULibSpigotPlugin.PAPER ? new Paper() : handler.new NoPaper());
    }

    // villagers can trade EVERY enchantment
    // 1.16: except soul speed
    static List<Enchantment> getObtainableVillagerEnchants() {
        XEnchantment soulSpeed = XEnchantment.SOUL_SPEED;

        return Arrays.stream(Enchantment.values())
                // filter out soul speed, if the MC version contains it
                .filter(enchantment -> !soulSpeed.isSupported() || enchantment != soulSpeed.parseEnchantment())
                .collect(Collectors.toList());
    }

    // villager trading
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handle(VillagerAcquireTradeEvent e) {

        if (!(e.getEntity() instanceof Villager))
            return;

        Villager v = (Villager) e.getEntity();
        if (v.getProfession() != Villager.Profession.LIBRARIAN)
            return;

        MerchantRecipe recipe = e.getRecipe();
        ItemStack result = recipe.getResult();

        if (result.getType() != Material.ENCHANTED_BOOK)
            return;

        // check if a custom enchantment is randomly chosen
        List<Enchantment> enchantments = getObtainableVillagerEnchants();
        Enchantment random = enchantments.get(new Random().nextInt(enchantments.size()));
        if (!isCustomEnchant(random))
            return;

        CustomEnchantment ce = (CustomEnchantment) random;

        if (!ce.isObtainableViaVillagerTrading())
            return;

        // bound is max level bc Random#nextInt can produce 0, meaning this calculation is similar to an array index
        int ceLevel = new Random().nextInt(ce.getMaxLevel()) + ce.getStartLevel();

        // create enchanted book
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) book.getItemMeta();
        meta.addStoredEnchant(ce, ceLevel, false);
        book.setItemMeta(meta);
        EnchantUtil.updateCustomEnchantmentLore(book);

        // create new trade
        MerchantRecipe newRecipe = new MerchantRecipe(book, recipe.getUses(), recipe.getMaxUses(),
                recipe.hasExperienceReward(), recipe.getVillagerExperience(), recipe.getPriceMultiplier());

        // https://minecraft.gamepedia.com/Trading#cite_note-enchanted-book-8

        int lower = 2 + 3 * ceLevel;
        int upper = 6 + 13 * ceLevel;

        int cost = new Random().nextInt(upper + 1) + lower;

        if (ce.isTreasure())
            cost *= 2;

        // max cost 64
        cost = Integer.min(cost, 64);

        newRecipe.setIngredients(Arrays.asList(new ItemStack(Material.EMERALD, cost), new ItemStack(Material.BOOK)));

        e.setRecipe(newRecipe);

    }

    // enchanting table
    @SneakyThrows
    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void handle(EnchantItemEvent e) {

        Random rand = new Random();

        // set seed to exp seed of player
        int xpSeed = (int) ReflectUtil.forceCall(PackageType.CRAFTBUKKIT_ENTITY.getClass("CraftPlayer"),
                e.getEnchanter(), String.format("getHandle().%s()", getEnchantmentSeedMethodName)
        );
        rand.setSeed(xpSeed);

        int enchantability = EnchantUtil.getItemEnchantability(e.getItem());

        int modifiedLevel = e.getExpLevelCost() + 1 + rand.nextInt(enchantability / 4 + 1) + rand.nextInt(enchantability / 4 + 1);
        float f = (rand.nextFloat() + rand.nextFloat() - 1.0F) * 0.15F;
        modifiedLevel = NMSMathHelper.clamp(Math.round((float) modifiedLevel + (float) modifiedLevel * f), 1, Integer.MAX_VALUE);

        List<NMSEnchantmentData> additionalEnchantments = NMSEnchantHelper.getEnchantmentDatas(modifiedLevel, e.getItem(), false);

        modifiedLevel *= Math.pow(.5, e.getEnchantsToAdd().size() - 1);

        while (rand.nextInt(50) <= modifiedLevel) {
            additionalEnchantments.removeIf(en -> e.getEnchantsToAdd().keySet().stream().anyMatch(en.enchantment::conflictsWith));

            if (additionalEnchantments.isEmpty()) {
                break;
            }

            NMSEnchantmentData data = NMSWeightedRandom.getRandomItem(rand, additionalEnchantments);
            e.getEnchantsToAdd().putIfAbsent(data.enchantment, data.enchantmentLevel);
            modifiedLevel /= 2;
        }


        pl.sync(() -> EnchantUtil.updateCustomEnchantmentLore(
                ((EnchantingInventory) e.getInventory()).getItem()
        ));
    }

    // anvil enchanting / combining
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void handle(PrepareAnvilEvent e) {
        final AnvilInventory inv = e.getInventory();
        final ItemStack target = inv.getItem(0);
        final ItemStack sacrifice = inv.getItem(1);

        if (target == null || target.getType() == Material.AIR || sacrifice == null || sacrifice.getType() == Material.AIR)
            return;

        Map<CustomEnchantment, Integer> existingCustomEnchants = EnchantUtil.getCustomItemEnchants(target);

        ItemStack result = e.getResult();
        if (result == null || result.getType() == Material.AIR) {
            result = target.clone();
        } else {
            // result was handled by vanilla or other plugin
            /*
              Add already existing custom enchants bc "result = e.getResult()" sets the result to an already merged item,
              which does not have to custom enchants anymore (bc vanilla minecraft skips them).
             */
            ItemStack res = result;
            existingCustomEnchants.forEach((ce, l) -> {
                if (res.getEnchantmentLevel(ce) > 0)
                    return;
                res.addUnsafeEnchantment(ce, l);
            });
            e.setResult(res);
        }


        Map<Enchantment, Integer> enchantmentsToAdd = EnchantUtil.getItemEnchants(sacrifice);
        if (enchantmentsToAdd.keySet().stream().noneMatch(this::isCustomEnchant))
            return;


        // add enchantments to result
        ItemMeta resultMeta = result.getItemMeta();
        int cost = inv.getRepairCost() + EnchantUtil.combineEnchantmentsSafe(result, resultMeta, sacrifice.getItemMeta());
        if (cost < 1)
            return; // no cost, means no combing at all
        //inv.setRepairCost(cost);
        EnchantUtil.setRepairCost(inv, cost);
        resultMeta.setDisplayName(e.getInventory().getRenameText());

        result.setItemMeta(resultMeta);
        EnchantUtil.updateCustomEnchantmentLore(result);

        e.setResult(result);
    }

    private boolean isCustomEnchant(Enchantment enchantment) {
        if (!(enchantment instanceof CustomEnchantment)) {
            // do not handle default enchantments
            return false;
        }
        // do not handle non-registered enchantments
        return EnchantUtil.getCustomEnchantments().contains(enchantment);
    }

    public static class Paper implements Listener {

        private Paper() {
        }

        @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
        public void handle(PrepareResultEvent e) {

            if (!(e.getInventory() instanceof GrindstoneInventory))
                return;
            ItemStack result = e.getResult();
            if (result == null)
                return;
            EnchantUtil.updateCustomEnchantmentLore(result);
        }

    }

    public class NoPaper implements Listener {

        private NoPaper() {
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        public void handle(InventoryClickEvent e) {
            Player p = (Player) e.getWhoClicked();
            InventoryView grindstone = p.getOpenInventory();
            if (grindstone.getType() != InventoryType.GRINDSTONE) {
                return;
            }
            pl.sync(() -> {
                ItemStack result = grindstone.getItem(2);
                if (result == null || result.getType() == Material.AIR)
                    return;

                EnchantUtil.updateCustomEnchantmentLore(result);
            });
        }

    }
}
