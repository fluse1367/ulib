package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.entry.MultiStateEntry;
import eu.software4you.spigot.inventorymenu.entry.ToggleableEntry;
import eu.software4you.ulib.ULib;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

public abstract class EntryFactory {

    // singleton
    private static EntryFactory instance;

    public static void setInstance(EntryFactory instance) {
        if (EntryFactory.instance != null)
            throw new IllegalStateException("InventoryMenu Entry-Factory already initialized");
        EntryFactory.instance = instance;
        ULib.getInstance().debugImplementation("InventoryMenu Entry-Factory");
    }

    static Entry createEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        return instance.implCreateEntry(representation, clickPermission, clickHandler);
    }

    static <T> MultiStateEntry<T> createMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission) {
        return instance.implCreateMultiStateEntry(defaultState, representation, representations, handlers, defaultClickHandler, clickPermission);
    }

    static ToggleableEntry createToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler) {
        return instance.implCreateToggleableEntry(representation, toggledRepresentation, clickPermission, handler);
    }

    abstract Entry implCreateEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler);

    abstract <T> MultiStateEntry<T> implCreateMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission);

    abstract ToggleableEntry implCreateToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler);
}
