package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.function.Consumer;

public class SinglePageMenuImpl extends PageImpl implements SinglePageMenu {
    public SinglePageMenuImpl(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        super(title, rows, entries, openHandler, closeHandler);
    }
}
