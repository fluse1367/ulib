package eu.software4you.ulib.spigot.inventorymenu.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an openable Inventory-Menu.
 *
 * @see SinglePageMenu
 * @see MultiPageMenu
 */
public interface Menu extends PageHandleable {
    /**
     * Shows the menu to a player.
     *
     * @param player the player the menu will be shown to
     */
    void open(@NotNull Player player);

    /**
     * Gets the title of this menu.
     *
     * @return the title
     */
    @NotNull
    String getTitle();
}
