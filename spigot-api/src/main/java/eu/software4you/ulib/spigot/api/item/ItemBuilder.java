package eu.software4you.ulib.spigot.api.item;

import com.google.common.collect.Multimap;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Can build {@link ItemStack}s fast and easy with one line.
 */
public class ItemBuilder {
    private ItemStack stack;
    private ItemMeta meta;

    /**
     * Wraps an already existing item.
     *
     * @param stack the item to wrap
     */
    public ItemBuilder(ItemStack stack) {
        this.stack = stack.clone();
        this.meta = stack.getItemMeta();
    }

    /**
     * Wraps an already existing item and meta.
     *
     * @param stack the item to wrap
     * @param meta  the meta to wrap
     */
    private ItemBuilder(ItemStack stack, ItemMeta meta) {
        this.stack = stack.clone();
        this.meta = meta.clone();
    }

    /**
     * @param material The material the {@link ItemStack} should have
     */
    public ItemBuilder(Material material) {
        this(material, 1);
    }

    /**
     * @param material The material the {@link ItemStack} should have
     * @param amount   The amount the {@link ItemStack} should have
     */
    public ItemBuilder(Material material, int amount) {
        this.stack = new ItemStack(material, amount);
        this.meta = this.stack.getItemMeta();
    }

    /**
     * Gets specific item metadata
     *
     * @param metaClass A compatible meta class (see <code>org.bukkit.inventory.meta</code> package)
     * @param <T>       The meta type
     * @return the specific item metadata, or {@code null} if meta is not an instance of provided type
     */
    public <T extends ItemMeta> T getMeta(Class<T> metaClass) {
        try {
            return (T) meta;
        } catch (ClassCastException e) {
            return null;
        }
    }

    /**
     * @return the item metadata
     */
    public ItemMeta getMeta() {
        return meta;
    }

    /**
     * Applies a function to the item meta.
     *
     * @param mod the function
     * @return this
     */
    public ItemBuilder meta(Consumer<ItemMeta> mod) {
        mod.accept(meta);
        return this;
    }

    /**
     * Applies a function to a specific item meta.
     *
     * @param metaClass A compatible meta class (see {@link org.bukkit.inventory.meta} package)
     * @param <T>       The meta type
     * @param mod       the function
     * @return this
     */
    public <T extends ItemMeta> ItemBuilder meta(Class<T> metaClass, Consumer<T> mod) {
        mod.accept(getMeta(metaClass));
        return this;
    }

    /**
     * Sets the amount of an item.
     *
     * @param amount the amount to set
     * @return this
     * @see ItemStack#setAmount(int)
     */
    public ItemBuilder amount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder customModelData(Integer data) {
        meta.setCustomModelData(data);
        return this;
    }

    /**
     * Sets a lore to the item
     *
     * @param lore the lines of the lore
     * @return this
     * @see ItemMeta#setLore(List)
     */
    public ItemBuilder lore(List<String> lore) {
        meta.setLore(lore);
        return this;
    }

    /**
     * Sets a lore to the item
     *
     * @param lines the lines of the lore
     * @return this
     * @see ItemMeta#setLore(List)
     */
    public ItemBuilder lore(String... lines) {
        meta.setLore(Arrays.asList(lines));
        return this;
    }

    /**
     * Sets the display name of the item.
     *
     * @param name the name of the item to display
     * @return this
     * @see ItemMeta#setDisplayName(String)
     */
    public ItemBuilder name(String name) {
        meta.setDisplayName(name);
        return this;
    }

    public ItemBuilder localizedName(String localizedName) {
        meta.setLocalizedName(localizedName);
        return this;
    }

    /**
     * Makes the item unbreakable.
     *
     * @return this
     */
    public ItemBuilder unbreakable() {
        return unbreakable(true);
    }

    /**
     * Specifies if the item is unbreakable.
     *
     * @param unbreakable true if the item should be unbreakable, false if not
     * @return this
     * @see ItemMeta#setUnbreakable(boolean)
     */
    public ItemBuilder unbreakable(boolean unbreakable) {
        meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Sets the attribute modifiers of the item.
     *
     * @param attributeModifiers the attributes with their modifiers
     * @return this
     * @see ItemMeta#setAttributeModifiers(Multimap)
     */
    public ItemBuilder modifiers(Multimap<Attribute, AttributeModifier> attributeModifiers) {
        meta.setAttributeModifiers(attributeModifiers);
        return this;
    }

    /**
     * Adds a modifier to an attribute of the item.
     *
     * @param attribute the attribute
     * @param modifier  the modifier
     * @return this
     * @see ItemMeta#addAttributeModifier(Attribute, AttributeModifier)
     */
    public ItemBuilder modifier(Attribute attribute, AttributeModifier modifier) {
        meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Adds flags to the item.
     *
     * @param flags the flags to add
     * @return this
     * @see ItemMeta#addItemFlags(ItemFlag...)
     */
    public ItemBuilder itemFlags(ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    /**
     * Adds an non-level restricted enchantment to the item
     *
     * @param ench  the enchantment
     * @param level the level
     * @return this
     * @see ItemMeta#addEnchant(Enchantment, int, boolean)
     */
    public ItemBuilder enchantment(Enchantment ench, int level) {
        meta.addEnchant(ench, level, true);
        return this;
    }

    /**
     * Sets the damage of the item, if it's capable of taking damage.
     *
     * @param damage the damage to set
     * @return this
     * @see Damageable#setDamage(int)
     */
    public ItemBuilder damage(int damage) {
        if (meta instanceof Damageable)
            ((Damageable) meta).setDamage(damage);
        return this;
    }

    /**
     * @see NBTEditor#set(Object, Object, Object...)
     */
    public ItemBuilder nbtTag(Object value, Object... keys) {
        stack.setItemMeta(meta.clone());
        stack = NBTEditor.set(stack, value, keys);
        meta = stack.getItemMeta();
        return this;
    }

    /**
     * Builds the {@link ItemStack}
     *
     * @return the built {@link ItemStack}
     */
    public ItemStack build() {
        stack.setItemMeta(meta.clone());
        return stack.clone();
    }
}
