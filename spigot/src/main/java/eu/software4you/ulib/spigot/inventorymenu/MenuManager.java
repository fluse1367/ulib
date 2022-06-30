package eu.software4you.ulib.spigot.inventorymenu;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.spigot.impl.inventorymenu.MenuManagerImpl;
import eu.software4you.ulib.spigot.inventorymenu.menu.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages {@link Menu} instances.
 * <p>You have to call {@link #listen()} in order to make the manager effective.
 */
public interface MenuManager {

    /**
     * Creates a new menu manager.
     *
     * @param plugin the plugin which requests the manager
     * @return the newly created menu manager
     */
    @NotNull
    static MenuManager of(@NotNull Plugin plugin) {
        return new MenuManagerImpl(plugin);
    }

    /**
     * Starts listening for the bukkit events in order to manage the menus. Effectively starts the manager.
     */
    void listen();

    /**
     * Stops listening for the bukkit events. Effectively stops the manager.
     */
    void stopListening();

    /**
     * Registers a new menu that should be managed.
     * Avoid registering a menu multiple times in different manager instances.
     *
     * @param menu the menu that should be managed
     */
    void registerMenu(@NotNull Menu menu);

    /**
     * Deletes a menu registration.
     *
     * @param menu the menu whose registration should be deleted
     */
    void unregisterMenu(@NotNull Menu menu);

    /**
     * Gets the respective page to an {@link Inventory}.
     *
     * @param inventory the inventory that the page displays
     * @return the page, or {@code null} of nothing found
     */
    @Nullable
    Page getPage(@NotNull Inventory inventory);

    /**
     * Gets the respective menu to an {@link Page}
     *
     * @param page the page that the menu displays
     * @return the menu, or {@code null} of nothing found
     */
    @Nullable
    Menu getMenu(@NotNull Page page);

    /**
     * Gets the respective menu to an {@link Inventory}
     *
     * @param inventory the inventory that the menu displays
     * @return the menu, or {@code null} of nothing found
     */
    @Nullable
    Menu getMenu(@NotNull Inventory inventory);

    /**
     * Attempts to find a page within a {@link MultiPageMenu}.
     *
     * @param page the page to search for
     * @return the respective {@link MultiPageMenu} and the page index, or {@code null} of nothing found
     */
    @Nullable
    Pair<MultiPageMenu, Integer> tryPage(@NotNull Page page);
}
