package eu.software4you.spigot.inventorymenu.builder;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.menu.Page;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A builder for a {@link Page}.
 */
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

    /**
     * Adds an entry to the next available inventory slot.
     *
     * @param entry the entry to add
     * @return this
     * @see eu.software4you.spigot.inventorymenu.menu.Page#setEntry(int, Entry)
     */
    public PageBuilder addEntry(Entry entry) {
        for (int i = 0; i < rows * 9; i++) {
            if (entries.containsKey(i))
                continue;
            setEntry(i, entry);
            break;
        }
        return this;
    }

    /**
     * Adds multiple entries to the next available inventory slots.
     *
     * @param entry   the entry to add
     * @param entries other entries to add
     * @return this
     * @see eu.software4you.spigot.inventorymenu.menu.Page#setEntry(int, Entry)
     */
    public PageBuilder addEntries(Entry entry, Entry... entries) {
        addEntry(entry);
        for (Entry en : entries) {
            addEntry(en);
        }
        return this;
    }

    /**
     * @see eu.software4you.spigot.inventorymenu.menu.Page#setEntry(int, Entry)
     */
    public PageBuilder setEntry(int slot, Entry entry) {
        entries.put(slot, entry);
        return this;
    }

    /**
     * @see eu.software4you.spigot.inventorymenu.menu.Page#setOpenHandler(Consumer)
     */
    public PageBuilder onOpen(Consumer<Player> handler) {
        this.openHandler = handler;
        return this;
    }

    /**
     * @see eu.software4you.spigot.inventorymenu.menu.Page#setCloseHandler(Consumer)
     */
    public PageBuilder onClose(Consumer<Player> handler) {
        this.closeHandler = handler;
        return this;
    }


    public Page build() {
        return MenuFactory.createPage(title, rows, entries, openHandler, closeHandler);
    }
}
