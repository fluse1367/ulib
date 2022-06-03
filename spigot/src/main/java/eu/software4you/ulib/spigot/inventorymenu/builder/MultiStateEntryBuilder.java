package eu.software4you.ulib.spigot.inventorymenu.builder;

import eu.software4you.ulib.spigot.impl.inventorymenu.MultiStateEntryImpl;
import eu.software4you.ulib.spigot.inventorymenu.entry.MultiStateEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * A builder for a {@link MultiStateEntry}.
 *
 * @param <T> the state type
 */
public class MultiStateEntryBuilder<T> extends EntryBuilder {
    private final Map<T, ItemStack> representations = new HashMap<>();
    private final Map<T, BiConsumer<Player, ClickType>> handlers = new HashMap<>();
    private final T defaultState;

    public MultiStateEntryBuilder(@NotNull T defaultState, @NotNull ItemStack defaultRepresentation) {
        super(defaultRepresentation);
        this.defaultState = defaultState;
    }

    /**
     * @see MultiStateEntry#setRepresentation(Object, ItemStack)
     */
    @NotNull
    @Contract("_, _ -> this")
    public MultiStateEntryBuilder<T> representation(@NotNull T state, @NotNull ItemStack representation) {
        if (defaultState.equals(state))
            throw new IllegalArgumentException("Cannot modify default state representation afterwards. Use the constructor to set it.");
        representations.put(state, representation.clone());
        return this;
    }

    /**
     * @see MultiStateEntry#setClickHandler(Object, BiConsumer)
     */
    @NotNull
    @Contract("_, _ -> this")
    public MultiStateEntryBuilder<T> onClick(@NotNull T state, @NotNull BiConsumer<Player, ClickType> handler) {
        if (state == defaultState)
            super.onClick(handler);
        else
            handlers.put(state, handler);
        return this;
    }

    /**
     * @see MultiStateEntry#setClickHandler(BiConsumer)
     */
    @Override
    @NotNull
    @Contract("_ -> this")
    public MultiStateEntryBuilder<T> onClick(@NotNull BiConsumer<Player, ClickType> handler) {
        super.onClick(handler);
        return this;
    }

    /**
     * @see MultiStateEntry#setClickPermission(String)
     */
    @Override
    @NotNull
    @Contract("_ -> this")
    public MultiStateEntryBuilder<T> clickPermission(@NotNull String clickPermission) {
        super.clickPermission(clickPermission);
        return this;
    }

    @Override
    @NotNull
    @Contract("-> new")
    public MultiStateEntry<T> build() {
        return new MultiStateEntryImpl<>(defaultState, representation, representations, handlers, onClick, clickPermission);
    }
}
