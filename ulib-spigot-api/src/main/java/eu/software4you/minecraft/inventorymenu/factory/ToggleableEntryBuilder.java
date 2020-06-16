package eu.software4you.minecraft.inventorymenu.factory;

import eu.software4you.minecraft.inventorymenu.entry.ToggleableEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class ToggleableEntryBuilder extends EntryBuilder {
    private final ItemStack toggledRepresentation;

    public ToggleableEntryBuilder(ItemStack representation, ItemStack toggledRepresentation) {
        super(representation);
        this.toggledRepresentation = toggledRepresentation;
    }

    @Override
    public ToggleableEntryBuilder clickPermission(String clickPermission) {
        super.clickPermission(clickPermission);
        return this;
    }

    @Override
    public ToggleableEntryBuilder onClick(BiConsumer<Player, ClickType> handler) {
        super.onClick(handler);
        return this;
    }

    public ToggleableEntry build() {
        return EntryFactory.createToggleableEntry(representation, toggledRepresentation, clickPermission, onClick);
    }
}
