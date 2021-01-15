package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

class SinglePageMenuImpl extends PageImpl implements SinglePageMenu {
    SinglePageMenuImpl(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        super(title, rows, entries, openHandler, closeHandler);
    }
}
