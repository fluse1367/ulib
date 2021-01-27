package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.entry.MultiStateEntry;
import eu.software4you.spigot.inventorymenu.entry.ToggleableEntry;
import eu.software4you.ulib.Await;
import eu.software4you.ulib.ImplRegistry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class EntryFactory {

    @Await
    private static EntryFactory impl;

    static Entry createEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        return impl.implCreateEntry(representation, clickPermission, clickHandler);
    }

    static <T> MultiStateEntry<T> createMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission) {
        return impl.implCreateMultiStateEntry(defaultState, representation, representations, handlers, defaultClickHandler, clickPermission);
    }

    static ToggleableEntry createToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler) {
        return impl.implCreateToggleableEntry(representation, toggledRepresentation, clickPermission, handler);
    }

    protected abstract Entry implCreateEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler);

    protected abstract <T> MultiStateEntry<T> implCreateMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission);

    protected abstract ToggleableEntry implCreateToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler);
}
