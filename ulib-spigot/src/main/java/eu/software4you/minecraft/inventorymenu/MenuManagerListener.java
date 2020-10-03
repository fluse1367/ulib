package eu.software4you.minecraft.inventorymenu;

import eu.software4you.common.collection.Pair;
import eu.software4you.minecraft.inventorymenu.entry.Entry;
import eu.software4you.minecraft.inventorymenu.entry.ToggleableEntry;
import eu.software4you.minecraft.inventorymenu.event.MenuCloseEvent;
import eu.software4you.minecraft.inventorymenu.event.MenuOpenEvent;
import eu.software4you.minecraft.inventorymenu.event.MenuSwitchPageEvent;
import eu.software4you.minecraft.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.minecraft.inventorymenu.menu.Menu;
import eu.software4you.minecraft.inventorymenu.menu.MultiPageMenu;
import eu.software4you.minecraft.inventorymenu.menu.Page;
import eu.software4you.minecraft.inventorymenu.menu.PageHandleable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MenuManagerListener extends MenuManager.Handler {
    private final MenuManager man;

    private final List<Pair<Player, MultiPageMenu>> noTriggerOpenClose = new ArrayList<>();

    public MenuManagerListener(MenuManager man) {
        this.man = man;
    }

    @EventHandler
    public void handle(InventoryClickEvent e) {
        Inventory inv = e.getClickedInventory();
        if (inv == null)
            return;
        Page page = man.getPage(inv);
        if (page == null)
            return;
        Player p = (Player) e.getWhoClicked();

        e.setCancelled(true);

        int slot = e.getSlot();

        boolean forwards = slot == page.getRows() * 9 - 1;
        if (forwards || slot == page.getRows() * 9 - 9) {

            Pair<MultiPageMenu, Integer> info = man.tryPage(page);
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
        Page opened = man.getPage(e.getInventory());
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
        Page closed = man.getPage(e.getInventory());
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
        Pair<MultiPageMenu, Integer> info = man.tryPage(page);
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

    @Override
    void clearBlacklist() {
        noTriggerOpenClose.clear();
    }
}
