package eu.software4you.ulib.spigot.inventorymenu.builder;

import eu.software4you.ulib.spigot.impl.inventorymenu.ToggleableEntryImpl;
import eu.software4you.ulib.spigot.inventorymenu.entry.ToggleableEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

/**
 * A builder for a  {@link ToggleableEntry}.
 */
public class ToggleableEntryBuilder extends EntryBuilder {
    private final ItemStack toggledRepresentation;

    public ToggleableEntryBuilder(ItemStack representation, ItemStack toggledRepresentation) {
        super(representation);
        this.toggledRepresentation = toggledRepresentation;
    }

    /**
     * @see ToggleableEntry#setClickPermission(String)
     */
    @Override
    public ToggleableEntryBuilder clickPermission(String clickPermission) {
        super.clickPermission(clickPermission);
        return this;
    }

    /**
     * @see ToggleableEntry#setClickHandler(BiConsumer)
     */
    @Override
    public ToggleableEntryBuilder onClick(BiConsumer<Player, ClickType> handler) {
        super.onClick(handler);
        return this;
    }

    public ToggleableEntry build() {
        return new ToggleableEntryImpl(representation, toggledRepresentation, clickPermission, onClick);
    }
}
