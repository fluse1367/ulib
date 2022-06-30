package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.spigot.impl.DelegationListener;
import eu.software4you.ulib.spigot.inventorymenu.MenuManager;
import eu.software4you.ulib.spigot.inventorymenu.menu.*;
import lombok.Synchronized;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class MenuManagerImpl implements MenuManager {

    private final Plugin plugin;
    private final MenuManagerListener handler;
    private final List<Menu> menus = new LinkedList<>();

    private Listener dummyListener;
    private boolean listening = false;

    public MenuManagerImpl(Plugin plugin) {
        this.plugin = plugin;
        this.handler = new MenuManagerListener(this);
    }

    private void registerHandles() {
        DelegationListener d = new DelegationListener();
        DelegationListener.registerDelegation(d, InventoryClickEvent.class, handler::handle,
                EventPriority.NORMAL, false, plugin);
        DelegationListener.registerDelegation(d, InventoryOpenEvent.class, handler::handle,
                EventPriority.NORMAL, false, plugin);
        DelegationListener.registerDelegation(d, InventoryCloseEvent.class, handler::handle,
                EventPriority.NORMAL, false, plugin);
        this.dummyListener = d;
    }

    private void reset() {
        handler.getNoTriggerOpenClose().clear();
    }

    @Override
    @Synchronized
    public void listen() {
        if (listening)
            return;
        registerHandles();
        reset();
        listening = true;
    }

    @Override
    @Synchronized
    public void stopListening() {
        if (!listening)
            return;
        HandlerList.unregisterAll(dummyListener);
        reset();
        listening = false;
    }

    @Override
    public void registerMenu(@NotNull Menu menu) {
        menus.add(menu);
    }

    @Override
    public void unregisterMenu(@NotNull Menu menu) {
        menus.remove(menu);
    }

    @Override
    public @Nullable Page getPage(@NotNull Inventory inventory) {
        for (Menu m : menus) {
            if (m instanceof Page page) {
                if (page.getInventory().equals(inventory))
                    return page;

                continue;
            }

            if (!(m instanceof MultiPageMenu menu))
                continue;

            for (Map.Entry<Integer, Page> entry : menu.getPages().entrySet()) {
                Page page = entry.getValue();
                if (page.getInventory().equals(inventory))
                    return page;
            }

        }
        return null;
    }

    @Override
    public @Nullable Menu getMenu(@NotNull Page page) {
        if (page instanceof SinglePageMenu)
            return (SinglePageMenu) page;

        for (Menu m : menus) {
            if (!(m instanceof MultiPageMenu menu)) {
                continue;
            }

            if (menu.getPages().values().stream()
                    .anyMatch(page::equals))
                return menu;
        }
        return null;
    }

    @Override
    public @Nullable Menu getMenu(@NotNull Inventory inventory) {
        var page = getPage(inventory);
        return page == null ? null : getMenu(page);
    }

    @Override
    public @Nullable Pair<MultiPageMenu, Integer> tryPage(@NotNull Page page) {
        for (Menu m : menus) {
            if (!(m instanceof MultiPageMenu menu))
                continue;

            var op = menu.getPages().entrySet().stream()
                    .filter(e -> e.getValue().equals(page))
                    .findFirst()
                    .map(Map.Entry::getKey)
                    .map(i -> new Pair<>(menu, i));

            if (op.isPresent())
                return op.get();
        }

        return null;
    }
}
