package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.spigot.inventorymenu.menu.MultiPageMenu;
import eu.software4you.spigot.inventorymenu.menu.Page;
import eu.software4you.spigot.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public abstract class MenuFactory {

    // singleton
    static MenuFactory instance;

    static Page createPage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return instance.implCreatePage(title, rows, entries, openHandler, closeHandler);
    }

    static SinglePageMenu createMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return instance.implCreateMenu(title, rows, entries, openHandler, closeHandler);
    }

    static MultiPageMenu createMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler) {
        return instance.implCreateMultiPageMenu(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }

    abstract Page implCreatePage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler);

    abstract SinglePageMenu implCreateMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler);

    abstract MultiPageMenu implCreateMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler);
}
