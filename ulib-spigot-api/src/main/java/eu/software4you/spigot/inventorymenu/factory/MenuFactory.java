package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.spigot.inventorymenu.menu.MultiPageMenu;
import eu.software4you.spigot.inventorymenu.menu.Page;
import eu.software4you.spigot.inventorymenu.menu.SinglePageMenu;
import eu.software4you.ulib.ImplRegistry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public abstract class MenuFactory {

    private static MenuFactory impl;

    private static MenuFactory impl() {
        if (impl == null) {
            impl = ImplRegistry.get(MenuFactory.class);
        }
        return impl;
    }

    static Page createPage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return impl.implCreatePage(title, rows, entries, openHandler, closeHandler);
    }

    static SinglePageMenu createMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return impl.implCreateMenu(title, rows, entries, openHandler, closeHandler);
    }

    static MultiPageMenu createMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler) {
        return impl.implCreateMultiPageMenu(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }

    protected abstract Page implCreatePage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler);

    protected abstract SinglePageMenu implCreateMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler);

    protected abstract MultiPageMenu implCreateMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler);
}
