package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.spigot.inventorymenu.MenuManager;
import eu.software4you.ulib.spigot.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.inventorymenu.entry.ToggleableEntry;
import eu.software4you.ulib.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.ulib.spigot.inventorymenu.menu.*;
import lombok.AccessLevel;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

final class MenuManagerListener {
    private final MenuManager man;

    @Getter(AccessLevel.PACKAGE)
    private final List<Pair<Player, MultiPageMenu>> noTriggerOpenClose = new ArrayList<>();

    MenuManagerListener(MenuManager man) {
        this.man = man;
    }

    void handle(InventoryClickEvent e) {
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
                return;
            }
        }

        Entry entry = page.getEntries().get(slot);
        if (entry == null)
            return;

        String perm = entry.getClickPermission();
        if (perm != null && !perm.isEmpty() && !p.hasPermission(perm))
            return;

        if (entry instanceof ToggleableEntry ten) {
            ten.setToggled(!ten.isToggled());
        }


        BiConsumer<Player, ClickType> handler = entry.getClickHandler();
        if (handler != null)
            handler.accept(p, e.getClick());
    }

    void handle(InventoryOpenEvent e) {
        Player p = (Player) e.getPlayer();
        Page opened = man.getPage(e.getInventory());
        if (opened == null)
            return;
        MultiPageMenu mpm = getMultiPageMenu(opened);
        if (mpm != null && !noTriggerOpenClose.contains(new Pair<>(p, mpm))) {
            handleOpen(mpm, p);
        }
        handleOpen(opened, p);
    }

    void handle(InventoryCloseEvent e) {
        Player p = (Player) e.getPlayer();
        Page closed = man.getPage(e.getInventory());
        if (closed == null)
            return;
        MultiPageMenu mpm = getMultiPageMenu(closed);
        if (mpm != null && !noTriggerOpenClose.contains(new Pair<>(p, mpm))) {
            handleClose(mpm, p);
        }
        handleClose(closed, p);
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
}
