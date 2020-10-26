package eu.software4you.minecraft.inventorymenu.factory;

import eu.software4you.minecraft.inventorymenu.entry.Entry;
import eu.software4you.minecraft.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.minecraft.inventorymenu.menu.MultiPageMenu;
import eu.software4you.minecraft.inventorymenu.menu.Page;
import eu.software4you.minecraft.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

public class MenuFactoryImpl extends MenuFactory {
    @Override
    Page implCreatePage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return new PageImpl(title, rows, entries, openHandler, closeHandler);
    }

    @Override
    SinglePageMenu implCreateMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return new SinglePageMenuImpl(title, rows, entries, openHandler, closeHandler);
    }

    @Override
    MultiPageMenu implCreateMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler) {
        return new MultiPageMenuImpl(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }
}