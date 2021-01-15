package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;

import java.util.function.Consumer;

public class MenuBuilder extends PageBuilder {
    public MenuBuilder(String title, int rows) {
        super(title, rows);
    }

    @Override
    public MenuBuilder addEntry(Entry entry) {
        super.addEntry(entry);
        return this;
    }

    @Override
    public MenuBuilder addEntries(Entry entry, Entry... entries) {
        super.addEntries(entry, entries);
        return this;
    }

    @Override
    public MenuBuilder setEntry(int slot, Entry entry) {
        super.setEntry(slot, entry);
        return this;
    }

    @Override
    public MenuBuilder onOpen(Consumer<Player> handler) {
        super.onOpen(handler);
        return this;
    }

    @Override
    public MenuBuilder onClose(Consumer<Player> handler) {
        super.onClose(handler);
        return this;
    }

    @Override
    public SinglePageMenu build() {
        return MenuFactory.createMenu(title, rows, entries, openHandler, closeHandler);
    }
}
