package eu.software4you.minecraft.inventorymenu.factory;

import eu.software4you.minecraft.inventorymenu.entry.Entry;
import eu.software4you.minecraft.inventorymenu.entry.MultiStateEntry;
import eu.software4you.minecraft.inventorymenu.entry.ToggleableEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public class EntryFactory {
    public static Entry createEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        return new EntryImpl(representation, clickPermission, clickHandler);
    }

    public static <T> MultiStateEntry<T> createMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission) {
        return new MultiStateEntryImpl<>(defaultState, representation, representations, handlers, defaultClickHandler, clickPermission);
    }

    public static ToggleableEntry createToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler) {
        return new ToggleableEntryImpl(representation, toggledRepresentation, clickPermission, handler);
    }
}