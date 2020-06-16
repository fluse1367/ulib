package eu.software4you.minecraft.inventorymenu;

import eu.software4you.common.collection.Pair;
import eu.software4you.minecraft.inventorymenu.entry.Entry;
import eu.software4you.minecraft.inventorymenu.entry.ToggleableEntry;
import eu.software4you.minecraft.inventorymenu.event.MenuCloseEvent;
import eu.software4you.minecraft.inventorymenu.event.MenuOpenEvent;
import eu.software4you.minecraft.inventorymenu.event.MenuSwitchPageEvent;
import eu.software4you.minecraft.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.minecraft.inventorymenu.menu.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Manages {@link Menu} instances.
 * <p>You have to call {@link #listen()} in order to make the manager effective.</p>
 */
public class MenuManager {
    private final Listener listener = new Listener();
    private final Plugin plugin;
    private final List<Menu> menus = new ArrayList<>();
    private boolean listening = false;

    public MenuManager(Plugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Starts listening for the bukkit events in order to manage the menus. Effectively starts the manager.
     */
    public void listen() {
        if (listening)
            return;
        listener.noTriggerOpenClose.clear();
        plugin.getServer().getPluginManager().registerEvents(listener, plugin);
        listening = true;
    }

    /**
     * Stops listening for the bukkit events. Effectively stops the manager.
     */
    public void stopListening() {
        if (!listening)
            return;
        HandlerList.unregisterAll(listener);
        listener.noTriggerOpenClose.clear();
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


    /**
     * @y.exclude
     */
    public class Listener implements org.bukkit.event.Listener {
        private final List<Pair<Player, MultiPageMenu>> noTriggerOpenClose = new ArrayList<>();

        @EventHandler
        public void handle(InventoryClickEvent e) {
            Inventory inv = e.getClickedInventory();
            if (inv == null)
                return;
            Page page = getPage(inv);
            if (page == null)
                return;
            Player p = (Player) e.getWhoClicked();

            e.setCancelled(true);

            int slot = e.getSlot();

            boolean forwards = slot == page.getRows() * 9 - 1;
            if (forwards || slot == page.getRows() * 9 - 9) {

                Pair<MultiPageMenu, Integer> info = tryPage(page);
                if (info != null) {
                    MultiPageMenu menu = info.getFirst();
                    int pageIndex = info.getSecond();
                    int nextIndex = pageIndex + (forwards ? +1 : -1);
                    Page nextPage = menu.getPage(nextIndex);
                    if (nextPage == null)
                        return;
                    Pair<Player, MultiPageMenu> pair = new Pair<>(p, menu);
                    noTriggerOpenClose.add(pair);
                    nextPage.open(p);
                    noTriggerOpenClose.remove(pair);
                    PageSwitchHandler handler = menu.getPageSwitchHandler();
                    if (handler != null)
                        handler.handle(p, page, pageIndex, nextPage, nextIndex);
                    Bukkit.getPluginManager().callEvent(new MenuSwitchPageEvent(p, menu,
                            page, pageIndex, nextPage, nextIndex));
                    return;
                }
            }

            Entry entry = page.getEntries().get(slot);
            if (entry == null)
                return;

            String perm = entry.getClickPermission();
            if (perm != null && !perm.isEmpty() && !p.hasPermission(perm))
                return;

            if (entry instanceof ToggleableEntry) {
                ToggleableEntry ten = (ToggleableEntry) entry;
                ten.setToggled(!ten.isToggled());
            }


            BiConsumer<Player, ClickType> handler = entry.getClickHandler();
            if (handler != null)
                handler.accept(p, e.getClick());
        }

        @EventHandler
        public void handle(InventoryOpenEvent e) {
            Player p = (Player) e.getPlayer();
            Page opened = getPage(e.getInventory());
            if (opened == null)
                return;
            MultiPageMenu mpm = getMultiPageMenu(opened);
            if (mpm != null && !noTriggerOpenClose.contains(new Pair<>(p, mpm))) {
                handleOpen(mpm, p);
                Bukkit.getPluginManager().callEvent(new MenuOpenEvent(p, mpm));
            }
            handleOpen(opened, p);
            if (opened instanceof Menu) {
                Bukkit.getPluginManager().callEvent(new MenuOpenEvent(p, (Menu) opened));
            }
        }

        @EventHandler
        public void handle(InventoryCloseEvent e) {
            Player p = (Player) e.getPlayer();
            Page closed = getPage(e.getInventory());
            if (closed == null)
                return;
            MultiPageMenu mpm = getMultiPageMenu(closed);
            if (mpm != null && !noTriggerOpenClose.contains(new Pair<>(p, mpm))) {
                handleClose(mpm, p);
                Bukkit.getPluginManager().callEvent(new MenuCloseEvent(p, mpm));
            }
            handleClose(closed, p);
            if (closed instanceof Menu) {
                Bukkit.getPluginManager().callEvent(new MenuCloseEvent(p, (Menu) closed));
            }
        }

        private MultiPageMenu getMultiPageMenu(Page page) {
            Pair<MultiPageMenu, Integer> info = tryPage(page);
            return info != null ? info.getFirst() : null;
        }

        private void handleClose(PageHandleable handleable, Player player) {
            handle(handleable.getCloseHandler(), player);
        }

        private void handleOpen(PageHandleable handleable, Player player) {
            handle(handleable.getOpenHandler(), player);
        }

        private void handle(Consumer<Player> handler, Player player) {
            if (handler != null)
                handler.accept(player);
        }
    }
}
