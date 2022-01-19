package eu.software4you.ulib.spigot.impl.inventorymenu.factory;

import eu.software4you.ulib.spigot.api.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.api.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

class SinglePageMenuImpl extends PageImpl implements SinglePageMenu {
    SinglePageMenuImpl(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        super(title, rows, entries, openHandler, closeHandler);
    }
}
