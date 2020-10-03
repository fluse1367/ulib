package eu.software4you.minecraft.inventorymenu.factory;

import eu.software4you.minecraft.inventorymenu.entry.Entry;
import eu.software4you.minecraft.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.minecraft.inventorymenu.menu.MultiPageMenu;
import eu.software4you.minecraft.inventorymenu.menu.Page;
import eu.software4you.minecraft.inventorymenu.menu.SinglePageMenu;
import eu.software4you.ulib.ULib;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public abstract class MenuFactory {

    // singleton
    private static MenuFactory instance;

    public static void setInstance(MenuFactory instance) {
        if (MenuFactory.instance != null)
            throw new IllegalStateException("InventoryMenu Menu-Factory already initialized");
        MenuFactory.instance = instance;
        ULib.getInstance().debugImplementation("InventoryMenu Menu-Factory");
    }

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
