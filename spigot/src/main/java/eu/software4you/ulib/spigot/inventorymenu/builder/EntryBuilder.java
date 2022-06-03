package eu.software4you.ulib.spigot.inventorymenu.builder;

import eu.software4you.ulib.spigot.impl.inventorymenu.EntryImpl;
import eu.software4you.ulib.spigot.inventorymenu.entry.Entry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * A builder for an {@link Entry}.
 */
public class EntryBuilder {
    protected final ItemStack representation;
    protected String clickPermission;
    protected BiConsumer<Player, ClickType> onClick = null;

    public EntryBuilder(@NotNull ItemStack representation) {
        this.representation = representation;
        this.clickPermission = "";
    }

    /**
     * @see Entry#setClickPermission(String)
     */
    @NotNull
    @Contract("_ -> this")
    public EntryBuilder clickPermission(@NotNull String clickPermission) {
        this.clickPermission = clickPermission;
        return this;
    }

    /**
     * @see Entry#setClickHandler(BiConsumer)
     */
    @NotNull
    @Contract("_ -> this")
    public EntryBuilder onClick(@NotNull BiConsumer<Player, ClickType> handler) {
        this.onClick = handler;
        return this;
    }

    @NotNull
    @Contract("-> new")
    public Entry build() {
        return new EntryImpl(representation, clickPermission, onClick);
    }
}
