package eu.software4you.ulib.spigot.api.inventorymenu.handlers;

import eu.software4you.ulib.spigot.api.inventorymenu.menu.MultiPageMenu;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.Page;
import org.bukkit.entity.Player;

/**
 * Represents an operation that handles the event when a player switches a {@link Page} in a {@link MultiPageMenu}.
 * Inspired by {@link java.util.function.Consumer} and {@link java.util.function.BiConsumer}.
 */
public interface PageSwitchHandler {
    /**
     * Handles the event / performs the operation
     *
     * @param player            the player that switches the page
     * @param previousPage      the page that was closed
     * @param previousPageIndex the index of the previous page
     * @param page              the page that was opened
     * @param pageIndex         the index of the page
     */
    void handle(Player player, Page previousPage, int previousPageIndex, Page page, int pageIndex);
}
