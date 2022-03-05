package eu.software4you.ulib.spigot.api.inventorymenu;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.spigot.api.internal.Providers.ProviderMenuManager;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.Menu;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.MultiPageMenu;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.Page;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.SinglePageMenu;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Manages {@link Menu} instances.
 * <p>You have to call {@link #listen()} in order to make the manager effective.
 */
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class MenuManager {

    private final Plugin plugin;
    private final List<Menu> menus = new ArrayList<>();
    private Listener handler;
    protected Supplier<Listener> handlerRegister;
    protected Runnable reset;
    private boolean listening = false;

    /**
     * Creates a new menu manager.
     *
     * @param plugin the plugin which requests the manager
     * @return the newly created menu manager
     */
    public static MenuManager of(Plugin plugin) {
        return Providers.get(ProviderMenuManager.class).provide(plugin);
    }

    /**
     * Starts listening for the bukkit events in order to manage the menus. Effectively starts the manager.
     */
    public void listen() {
        if (listening)
            return;
        this.handler = handlerRegister.get();
        reset.run();
        listening = true;
    }

    /**
     * Stops listening for the bukkit events. Effectively stops the manager.
     */
    public void stopListening() {
        if (!listening)
            return;
        HandlerList.unregisterAll(handler);
        reset.run();
        listening = false;
    }

    /**
     * Registers a new menu that should be managed.
     * Avoid registering a menu multiple times in different manager instances.
     *
     * @param menu the menu that should be managed
     */
    public void registerMenu(Menu menu) {
        menus.add(menu);
    }

    /**
     * Deletes a menu registration.
     *
     * @param menu the menu whose registration should be deleted
     */
    public void unregisterMenu(Menu menu) {
        menus.remove(menu);
    }

    /**
     * Gets the respective page to an {@link Inventory}.
     *
     * @param inventory the inventory that the page displays
     * @return the page, or {@code null} of nothing found
     */
    public Page getPage(Inventory inventory) {
        for (Menu m : menus) {
            if (m instanceof MultiPageMenu menu) {
                for (Map.Entry<Integer, Page> entry : menu.getPages().entrySet()) {
                    Page page = entry.getValue();
                    if (page.getInventory().equals(inventory))
                        return page;
                }
            } else if (m instanceof Page page) {
                if (page.getInventory().equals(inventory))
                    return page;
            }
        }
        return null;
    }

    /**
     * Gets the respective menu to an {@link Page}
     *
     * @param page the page that the menu displays
     * @return the menu, or {@code null} of nothing found
     */
    public Menu getMenu(Page page) {
        if (page instanceof SinglePageMenu)
            return (SinglePageMenu) page;

        for (Menu m : menus) {
            if (m instanceof MultiPageMenu menu) {
                for (Map.Entry<Integer, Page> entry : menu.getPages().entrySet()) {
                    if (entry.getValue().equals(page))
                        return menu;
                }
            }
        }
        return null;
    }

    /**
     * Gets the respective menu to an {@link Inventory}
     *
     * @param inventory the inventory that the menu displays
     * @return the menu, or {@code null} of nothing found
     */
    public Menu getMenu(Inventory inventory) {
        return getMenu(getPage(inventory));
    }

    /**
     * Attempts to find a page within a {@link MultiPageMenu}.
     *
     * @param page the page to search for
     * @return the respective {@link MultiPageMenu} and the page index, or {@code null} of nothing found
     */
    public Pair<MultiPageMenu, Integer> tryPage(Page page) {
        for (Menu m : menus) {
            if (m instanceof MultiPageMenu menu) {
                for (Map.Entry<Integer, Page> entry : menu.getPages().entrySet()) {
                    if (entry.getValue().equals(page))
                        return new Pair<>(menu, entry.getKey());
                }
            }
        }
        return null;
    }
}
