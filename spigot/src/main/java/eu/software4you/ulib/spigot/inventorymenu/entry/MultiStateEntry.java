package eu.software4you.ulib.spigot.inventorymenu.entry;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.*;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * Represents an {@link Entry} which can have multiple states.
 * Each state comes with its own {@link ItemStack} that is being displayed and its own click handlers.
 *
 * @param <T> The type of a state.
 */
public interface MultiStateEntry<T> extends Entry {
    /**
     * Gets the current state of this entry.
     *
     * @return the current state
     */
    @NotNull
    T getState();

    /**
     * Sets the current state of this entry.
     *
     * @param state the state to set
     */
    void setState(@NotNull T state);

    /**
     * Gets the default state of this entry.
     *
     * @return the default state
     */
    @NotNull
    T getDefaultState();

    /**
     * Sets the default state of this entry.
     *
     * @param state the state to set
     */
    void setDefaultState(@NotNull T state);

    /**
     * Gets the representing item stack in the inventory associated with the state.
     *
     * @return the representing item stack
     */
    @Nullable
    ItemStack getRepresentation(@NotNull T state);

    /**
     * Sets the representing item stack for a specific state.
     *
     * @param state          the state to set the item for
     * @param representation the item stack
     */
    // TODO: provide way to reset state to default representation
    void setRepresentation(@NotNull T state, @NotNull ItemStack representation);

    /**
     * Gets the default state representation.
     *
     * @return the default state representation
     */
    @NotNull
    default ItemStack getDefaultRepresentation() {
        return Objects.requireNonNull(getRepresentation(getDefaultState()));
    }

    /**
     * Gets all states of this entry and their associated representation.
     *
     * @return an immutable map with all states and their associated representation
     */
    @NotNull
    @UnmodifiableView
    Map<T, ItemStack> getRepresentations();


    /**
     * Sets the handler that will be called on a successful click from a player at a certain state.
     * If no handler is set for a state the general handler will be called.
     *
     * @param state   the state to set the handler for
     * @param handler the handler that will be called, or {@code null} to remove the handler
     */
    void setClickHandler(@NotNull T state, @Nullable BiConsumer<Player, ClickType> handler);

    /**
     * Gets the handler that will be called on a successful click from a player at a certain state.
     *
     * @param state the state to set the handler for
     * @return the handler that will be called on a successful click from a player at a certain state
     */
    @Nullable
    BiConsumer<Player, ClickType> getClickHandler(@NotNull T state);
}
