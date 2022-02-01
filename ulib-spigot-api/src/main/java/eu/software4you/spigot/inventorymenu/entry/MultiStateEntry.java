package eu.software4you.spigot.inventorymenu.entry;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
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
    T getState();

    /**
     * Sets the current state of this entry.
     *
     * @param state the state to set
     */
    void setState(T state);

    /**
     * Gets the default state of this entry.
     *
     * @return the default state
     */
    T getDefaultState();

    /**
     * Sets the default state of this entry.
     *
     * @param state the state to set
     */
    void setDefaultState(T state);

    /**
     * Gets the representing item stack in the inventory associated with the state.
     *
     * @return the representing item stack
     */
    ItemStack getRepresentation(T state);

    /**
     * Sets the representing item stack for a specific state.
     *
     * @param state          the state to set the item for
     * @param representation the item stack
     */
    void setRepresentation(T state, ItemStack representation);

    /**
     * Gets the default state representation.
     *
     * @return the default state representation
     */
    default ItemStack getDefaultRepresentation() {
        return getRepresentation(getDefaultState());
    }

    /**
     * Gets all states of this entry and their associated representation.
     *
     * @return an immutable map with all states and their associated representation
     */
    Map<T, ItemStack> getRepresentations();


    /**
     * Sets the handler that will be called on a successful click from a player at a certain state.
     * If no handler is set for a state the general handler will be called.
     *
     * @param state   the state to set the handler for
     * @param handler the handler that will be called, or {@code null} to remove the handler
     */
    void setClickHandler(T state, BiConsumer<Player, ClickType> handler);

    /**
     * Gets the handler that will be called on a successful click from a player at a certain state.
     *
     * @param state the state to set the handler for
     * @return the handler that will be called on a successful click from a player at a certain state
     */
    BiConsumer<Player, ClickType> getClickHandler(T state);
}
