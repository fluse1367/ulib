package eu.software4you.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.MultiStateEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class MultiStateEntryBuilder<T> extends EntryBuilder {
    private final Map<T, ItemStack> representations = new HashMap<>();
    private final Map<T, BiConsumer<Player, ClickType>> handlers = new HashMap<>();
    private final T defaultState;

    public MultiStateEntryBuilder(T defaultState, ItemStack defaultRepresentation) {
        super(defaultRepresentation);
        this.defaultState = defaultState;
    }

    public MultiStateEntryBuilder<T> representation(T state, ItemStack representation) {
        if (defaultState.equals(state))
            throw new IllegalArgumentException("Cannot modify default state representation afterwards. Use the constructor to set it.");
        representations.put(state, representation.clone());
        return this;
    }

    public MultiStateEntryBuilder<T> onClick(T state, BiConsumer<Player, ClickType> handler) {
        if (state == defaultState)
            super.onClick(handler);
        else
            handlers.put(state, handler);
        return this;
    }

    @Override
    public MultiStateEntryBuilder<T> clickPermission(String clickPermission) {
        super.clickPermission(clickPermission);
        return this;
    }

    @Override
    public MultiStateEntryBuilder<T> onClick(BiConsumer<Player, ClickType> handler) {
        super.onClick(handler);
        return this;
    }

    @Override
    public MultiStateEntry<T> build() {
        return EntryFactory.createMultiStateEntry(defaultState, representation, representations, handlers, onClick, clickPermission);
    }
}
