package eu.software4you.spigot.inventorymenu.menu;

import eu.software4you.common.collection.Pair;
import eu.software4you.spigot.inventorymenu.handlers.PageSwitchHandler;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Represents a {@link Menu}, with multiple pages.
 * Each page get own page switch buttons, if needed.
 *
 * @see eu.software4you.spigot.inventorymenu.factory.MultiPageMenuBuilder
 * @see eu.software4you.spigot.inventorymenu.factory.PageBuilder
 */
public interface MultiPageMenu extends Menu {
    /**
     * Gets the pages. The key is the page index.
     *
     * @return an immutable map with all pages
     */
    Map<Integer, Page> getPages();

    /**
     * Gets a page.
     *
     * @param index the page index
     * @return the page, or null if page does not exist
     */
    Page getPage(int index);

    /**
     * Sets a page.
     *
     * @param index the index to set
     * @param page  the page to set, or {@code null} to remove the page
     * @throws IndexOutOfBoundsException if the index is negative
     * @throws IllegalArgumentException  if page would be created with a gap to another page
     */
    void setPage(int index, Page page);

    /**
     * Sets the page switch buttons. They will only be displayed if needed.
     * If one of them or both are {@code null}, the button will be disabled.
     * If not called (and thus not set) the default buttons will be taken.
     *
     * @param previousPageButton the button to click for the previous page
     * @param nextPageButton     the button to click to the next page
     */
    void setPageSwitchButtons(ItemStack previousPageButton, ItemStack nextPageButton);

    /**
     * Gets the page switch buttons.
     * {@link Pair#getFirst()} is the previous page button, {@link Pair#getSecond()} is the next page button.
     *
     * @return a copy of the page switch buttons
     */
    Pair<ItemStack, ItemStack> getPageSwitchButtons();

    /**
     * Shows a page to a player.
     *
     * @param player    the player the page will be shown to
     * @param pageIndex the page to show
     */
    void open(Player player, int pageIndex);

    /**
     * Gets the handler that will be called when a player switches the page to another.
     *
     * @return the handler that will be called when a player switches the page to another
     */
    PageSwitchHandler getPageSwitchHandler();

    /**
     * Sets the handler that will be called when a player switches the page to another.
     *
     * @param handler the handler that will be called when a player switches the page to another
     */
    void setPageSwitchHandler(PageSwitchHandler handler);
}
