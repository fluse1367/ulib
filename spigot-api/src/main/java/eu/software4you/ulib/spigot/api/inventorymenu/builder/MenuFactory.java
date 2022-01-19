package eu.software4you.ulib.spigot.api.inventorymenu.builder;

import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.spigot.api.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.api.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.MultiPageMenu;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.Page;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public abstract class MenuFactory {

    private static MenuFactory getInstance() {
        return Providers.get(MenuFactory.class);
    }

    static Page createPage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return getInstance().implCreatePage(title, rows, entries, openHandler, closeHandler);
    }

    static SinglePageMenu createMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return getInstance().implCreateMenu(title, rows, entries, openHandler, closeHandler);
    }

    static MultiPageMenu createMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler) {
        return getInstance().implCreateMultiPageMenu(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }

    protected abstract Page implCreatePage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler);

    protected abstract SinglePageMenu implCreateMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler);

    protected abstract MultiPageMenu implCreateMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler);
}
