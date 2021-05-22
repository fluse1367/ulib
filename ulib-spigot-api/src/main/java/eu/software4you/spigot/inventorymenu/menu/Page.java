package eu.software4you.spigot.inventorymenu.menu;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.Map;

/**
 * Represents a single page of a {@link Menu} and thus an {@link Inventory}.
 *
 * @see eu.software4you.spigot.inventorymenu.builder.PageBuilder
 */
public interface Page extends PageHandleable {
    /**
     * Gets the title of this page.
     *
     * @return the title
     */
    String getTitle();

    /**
     * Gets row count of this page. Each row consists out of 9 slots.
     * The row count * 9 is equal to the inventory's size.
     *
     * @return the row count of this page
     */
    int getRows();

    /**
     * Gets all menu entries of this page excluding the page switch buttons.
     * The key is the slot index.
     *
     * @return a immutable map with all entries
     */
    Map<Integer, Entry> getEntries();

    /**
     * Sets an entry of this page.
     *
     * @param slot  the index of the {@link Inventory} slot
     * @param entry the entry to set, or null to clear the slot
     * @throws IllegalArgumentException if the slot is reserved for a page switch button
     * @throws IllegalStateException    if the entry belongs to another page
     */
    void setEntry(int slot, Entry entry);

    /**
     * Gets the inventory instance.
     *
     * @return the inventory
     */
    Inventory getInventory();

    /**
     * Shows this page to a player.
     *
     * @param player the player this page will be shown to
     */
    void open(Player player);
}
