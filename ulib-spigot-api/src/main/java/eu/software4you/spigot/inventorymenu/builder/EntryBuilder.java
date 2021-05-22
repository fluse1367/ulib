package eu.software4you.spigot.inventorymenu.builder;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * A builder for an {@link Entry}.
 */
public class EntryBuilder {
    protected final ItemStack representation;
    protected String clickPermission;
    protected BiConsumer<Player, ClickType> onClick = null;

    public EntryBuilder(ItemStack representation) {
        this.representation = representation;
        this.clickPermission = "";
    }

    /**
     * @see Entry#setClickPermission(String)
     */
    public EntryBuilder clickPermission(String clickPermission) {
        this.clickPermission = clickPermission;
        return this;
    }

    /**
     * @see Entry#setClickHandler(BiConsumer)
     */
    public EntryBuilder onClick(BiConsumer<Player, ClickType> handler) {
        this.onClick = handler;
        return this;
    }

    public Entry build() {
        return EntryFactory.createEntry(representation, clickPermission, onClick);
    }
}
