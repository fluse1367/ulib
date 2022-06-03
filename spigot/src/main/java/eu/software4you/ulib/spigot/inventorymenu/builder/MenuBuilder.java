package eu.software4you.ulib.spigot.inventorymenu.builder;

import eu.software4you.ulib.spigot.impl.inventorymenu.SinglePageMenuImpl;
import eu.software4you.ulib.spigot.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.inventorymenu.menu.SinglePageMenu;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.*;

import java.util.function.Consumer;

/**
 * A builder for a {@link SinglePageMenu}.
 */
public class MenuBuilder extends PageBuilder {
    public MenuBuilder(@NotNull String title, @Range(from = 1, to = 6) int rows) {
        super(title, rows);
    }

    /**
     * Adds an entry to the next available inventory slot.
     *
     * @param entry the entry to add
     * @return this
     * @see eu.software4you.ulib.spigot.inventorymenu.menu.Page#setEntry(int, Entry)
     */
    @Override
    @NotNull
    @Contract("_ -> this")
    public MenuBuilder addEntry(@NotNull Entry entry) {
        super.addEntry(entry);
        return this;
    }

    /**
     * Adds multiple entries to the next available inventory slots.
     *
     * @param entry   the entry to add
     * @param entries other entries to add
     * @return this
     * @see eu.software4you.ulib.spigot.inventorymenu.menu.Page#setEntry(int, Entry)
     */
    @Override
    @NotNull
    @Contract("_, _ -> this")
    public MenuBuilder addEntries(@NotNull Entry entry, @NotNull Entry... entries) {
        super.addEntries(entry, entries);
        return this;
    }

    /**
     * @see eu.software4you.ulib.spigot.inventorymenu.menu.Page#setEntry(int, Entry)
     */
    @Override
    @NotNull
    @Contract("_, _ -> this")
    public MenuBuilder setEntry(int slot, @NotNull Entry entry) {
        super.setEntry(slot, entry);
        return this;
    }

    /**
     * @see eu.software4you.ulib.spigot.inventorymenu.menu.Page#setOpenHandler(Consumer)
     */
    @Override
    @NotNull
    @Contract("_ -> this")
    public MenuBuilder onOpen(@NotNull Consumer<Player> handler) {
        super.onOpen(handler);
        return this;
    }

    /**
     * @see eu.software4you.ulib.spigot.inventorymenu.menu.Page#setCloseHandler(Consumer)
     */
    @Override
    @NotNull
    @Contract("_ -> this")
    public MenuBuilder onClose(@NotNull Consumer<Player> handler) {
        super.onClose(handler);
        return this;
    }

    @Override
    @NotNull
    @Contract("-> new")
    public SinglePageMenu build() {
        return new SinglePageMenuImpl(title, rows, entries, openHandler, closeHandler);
    }
}
