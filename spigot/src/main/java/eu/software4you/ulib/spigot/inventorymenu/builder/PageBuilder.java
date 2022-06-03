package eu.software4you.ulib.spigot.inventorymenu.builder;

import eu.software4you.ulib.spigot.impl.inventorymenu.PageImpl;
import eu.software4you.ulib.spigot.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.inventorymenu.menu.Page;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.*;

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

    public PageBuilder(@NotNull String title, @Range(from = 1, to = 6) int rows) {
        this.title = title;
        this.rows = rows;
    }

    /**
     * Adds an entry to the next available inventory slot.
     *
     * @param entry the entry to add
     * @return this
     * @see Page#setEntry(int, Entry)
     */
    @NotNull
    @Contract("_ -> this")
    public PageBuilder addEntry(@NotNull Entry entry) {
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
     * @see Page#setEntry(int, Entry)
     */
    @NotNull
    @Contract("_, _ -> this")
    public PageBuilder addEntries(@NotNull Entry entry, @NotNull Entry... entries) {
        addEntry(entry);
        for (Entry en : entries) {
            addEntry(en);
        }
        return this;
    }

    /**
     * @see Page#setEntry(int, Entry)
     */
    @NotNull
    @Contract("_, _ -> this")
    public PageBuilder setEntry(int slot, @NotNull Entry entry) {
        entries.put(slot, entry);
        return this;
    }

    /**
     * @see Page#setOpenHandler(Consumer)
     */
    @NotNull
    @Contract("_ -> this")
    public PageBuilder onOpen(@NotNull Consumer<Player> handler) {
        this.openHandler = handler;
        return this;
    }

    /**
     * @see Page#setCloseHandler(Consumer)
     */
    @NotNull
    @Contract("_ -> this")
    public PageBuilder onClose(@NotNull Consumer<Player> handler) {
        this.closeHandler = handler;
        return this;
    }


    @NotNull
    @Contract("-> new")
    public Page build() {
        return new PageImpl(title, rows, entries, openHandler, closeHandler);
    }
}
