package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.inventorymenu.entry.MultiStateEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

public class MultiStateEntryImpl<T> extends EntryImpl implements MultiStateEntry<T> {
    private final Map<T, ItemStack> representations;
    private final Map<T, BiConsumer<Player, ClickType>> handlers;
    private T defaultState;
    private T currentState;

    public MultiStateEntryImpl(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission) {
        super(representation, clickPermission, defaultClickHandler);
        this.representations = representations;
        this.handlers = handlers;
        this.defaultState = defaultState;
        this.currentState = defaultState;
        this.representations.put(currentState, representation);
    }

    @Override
    public T getState() {
        return currentState;
    }

    @Override
    public void setState(T state) {
        currentState = state;
        ItemStack representation = representations.get(state);
        if (representation == null)
            representation = getDefaultRepresentation();
        setRepresentation(representation);
    }

    @Override
    public T getDefaultState() {
        return defaultState;
    }

    @Override
    public void setDefaultState(T state) {
        if (currentState.equals(defaultState))
            setState(state);
        defaultState = state;
    }

    @Override
    public ItemStack getRepresentation(T state) {
        ItemStack representation = representations.get(state);
        if (representation == null)
            return null;
        return representation.clone();
    }

    @Override
    public void setRepresentation(T state, ItemStack representation) {
        ItemStack stack = representation.clone();
        representations.put(state, stack);
        if (state.equals(currentState))
            setRepresentation(stack);
    }

    @Override
    public Map<T, ItemStack> getRepresentations() {
        return Collections.unmodifiableMap(representations);
    }

    @Override
    public void setClickHandler(T state, BiConsumer<Player, ClickType> handler) {
        if (handler == null)
            handlers.remove(state);
        else
            handlers.put(state, handler);
    }

    @Override
    public BiConsumer<Player, ClickType> getClickHandler(T state) {
        return handlers.get(state);
    }

    @Override
    public BiConsumer<Player, ClickType> getClickHandler() {
        BiConsumer<Player, ClickType> currentHandler = handlers.get(currentState);
        return currentHandler != null ? currentHandler : super.getClickHandler();
    }
}
