package eu.software4you.ulib.impl.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.builder.MenuFactory;
import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.spigot.inventorymenu.menu.MultiPageMenu;
import eu.software4you.spigot.inventorymenu.menu.Page;
import eu.software4you.spigot.inventorymenu.menu.SinglePageMenu;
import eu.software4you.ulib.inject.Impl;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Impl(MenuFactory.class)
final class MenuFactoryImpl extends MenuFactory {
    @Override
    protected Page implCreatePage(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return new PageImpl(title, rows, entries, openHandler, closeHandler);
    }

    @Override
    protected SinglePageMenu implCreateMenu(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        return new SinglePageMenuImpl(title, rows, entries, openHandler, closeHandler);
    }

    @Override
    protected MultiPageMenu implCreateMultiPageMenu(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler) {
        return new MultiPageMenuImpl(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }
}
