package eu.software4you.ulib.spigot.api.inventorymenu.builder;

import eu.software4you.ulib.core.api.internal.Providers;
import eu.software4you.ulib.spigot.api.inventorymenu.entry.Entry;
import eu.software4you.ulib.spigot.api.inventorymenu.entry.MultiStateEntry;
import eu.software4you.ulib.spigot.api.inventorymenu.entry.ToggleableEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class EntryFactory {

    private static EntryFactory getInstance() {
        return Providers.get(EntryFactory.class);
    }

    static Entry createEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        return getInstance().implCreateEntry(representation, clickPermission, clickHandler);
    }

    static <T> MultiStateEntry<T> createMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission) {
        return getInstance().implCreateMultiStateEntry(defaultState, representation, representations, handlers, defaultClickHandler, clickPermission);
    }

    static ToggleableEntry createToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler) {
        return getInstance().implCreateToggleableEntry(representation, toggledRepresentation, clickPermission, handler);
    }

    protected abstract Entry implCreateEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler);

    protected abstract <T> MultiStateEntry<T> implCreateMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission);

    protected abstract ToggleableEntry implCreateToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler);
}
