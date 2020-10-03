package eu.software4you.minecraft.inventorymenu;

import eu.software4you.common.collection.Pair;
import eu.software4you.minecraft.inventorymenu.menu.Menu;
import eu.software4you.minecraft.inventorymenu.menu.MultiPageMenu;
import eu.software4you.minecraft.inventorymenu.menu.Page;
import eu.software4you.minecraft.inventorymenu.menu.SinglePageMenu;
import eu.software4you.ulib.ULib;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Manages {@link Menu} instances.
 * <p>You have to call {@link #listen()} in order to make the manager effective.</p>
 */
public class MenuManager {

    private static Function<MenuManager, Handler> handlerFunction;
    private final Handler handler = handlerFunction.apply(this);
    private final Plugin plugin;
    private final List<Menu> menus = new ArrayList<>();
    private boolean listening = false;

    public MenuManager(Plugin plugin) {
        this.plugin = plugin;
    }

    public static void setHandlerFunction(Function<MenuManager, ? extends Listener> handlerFunction) {
        if (MenuManager.handlerFunction != null)
            throw new IllegalStateException("InventoryMenu Menu-Manager already initialized");
        MenuManager.handlerFunction = (Function<MenuManager, Handler>) handlerFunction;
        ULib.getInstance().debugImplementation("InventoryMenu Menu-Manager");
    }

    /**
     * Starts listening for the bukkit events in order to manage the menus. Effectively starts the manager.
     */
    public void listen() {
        if (listening)
            return;
        handler.clearBlacklist();
        plugin.getServer().getPluginManager().registerEvents(handler, plugin);
        listening = true;
    }

    /**
     * Stops listening for the bukkit events. Effectively stops the manager.
     */
    public void stopListening() {
        if (!listening)
            return;
        HandlerList.unregisterAll(handler);
        handler.clearBlacklist();
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
     * Gets the respective page to an {@link Inventory}.
     *
     * @param inventory the inventory that the page displays
     * @return the page, or null of nothing found
     */
    public Page getPage(Inventory inventory) {
        for (Menu m : menus) {
            if (m instanceof MultiPageMenu) {
                MultiPageMenu menu = (MultiPageMenu) m;
                for (Map.Entry<Integer, Page> entry : menu.getPages().entrySet()) {
                    Page page = entry.getValue();
                    if (page.getInventory().equals(inventory))
                        return page;
                }
            } else if (m instanceof Page) {
                Page page = (Page) m;
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
     * @return the menu, or null of nothing found
     */
    public Menu getMenu(Page page) {
        if (page instanceof SinglePageMenu)
            return (SinglePageMenu) page;

        for (Menu m : menus) {
            if (m instanceof MultiPageMenu) {
                MultiPageMenu menu = (MultiPageMenu) m;
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
     * @return the menu, or null of nothing found
     */
    public Menu getMenu(Inventory inventory) {
        return getMenu(getPage(inventory));
    }

    /**
     * Tries to find a page within a {@link MultiPageMenu}.
     *
     * @param page the page to search for
     * @return the respective {@link MultiPageMenu} and the page index, or null of nothing found
     */
    public Pair<MultiPageMenu, Integer> tryPage(Page page) {
        for (Menu m : menus) {
            if (m instanceof MultiPageMenu) {
                MultiPageMenu menu = (MultiPageMenu) m;
                for (Map.Entry<Integer, Page> entry : menu.getPages().entrySet()) {
                    if (entry.getValue().equals(page))
                        return new Pair<>(menu, entry.getKey());
                }
            }
        }
        return null;
    }

    abstract static class Handler implements Listener {
        abstract void clearBlacklist();
    }
}
