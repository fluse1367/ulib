package eu.software4you.minecraft.inventorymenu.factory;

import eu.software4you.minecraft.inventorymenu.entry.Entry;
import eu.software4you.minecraft.inventorymenu.menu.Page;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PageBuilder {
    protected final String title;
    protected final int rows;
    protected final Map<Integer, Entry> entries = new HashMap<>();
    protected Consumer<Player> openHandler = null;
    protected Consumer<Player> closeHandler = null;

    public PageBuilder(String title, int rows) {
        this.title = title;
        this.rows = rows;
    }

    public PageBuilder addEntry(Entry entry) {
        for (int i = 0; i < rows * 9; i++) {
            if (entries.containsKey(i))
                continue;
            setEntry(i, entry);
            break;
        }
        return this;
    }

    public PageBuilder addEntries(Entry entry, Entry... entries) {
        addEntry(entry);
        for (Entry en : entries) {
            addEntry(en);
        }
        return this;
    }

    public PageBuilder setEntry(int slot, Entry entry) {
        entries.put(slot, entry);
        return this;
    }

    public PageBuilder onOpen(Consumer<Player> handler) {
        this.openHandler = handler;
        return this;
    }

    public PageBuilder onClose(Consumer<Player> handler) {
        this.closeHandler = handler;
        return this;
    }


    public Page build() {
        return MenuFactory.createPage(title, rows, entries, openHandler, closeHandler);
    }
}
