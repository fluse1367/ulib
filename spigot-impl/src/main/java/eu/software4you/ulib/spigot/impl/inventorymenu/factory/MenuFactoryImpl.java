package eu.software4you.ulib.spigot.impl.inventorymenu.factory;

import eu.software4you.ulib.spigot.api.inventorymenu.builder.MenuFactory;
import eu.software4you.ulib.spigot.api.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.api.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.MultiPageMenu;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.Page;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.SinglePageMenu;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.Consumer;

@NoArgsConstructor(access = AccessLevel.PUBLIC)
public final class MenuFactoryImpl extends MenuFactory {
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
