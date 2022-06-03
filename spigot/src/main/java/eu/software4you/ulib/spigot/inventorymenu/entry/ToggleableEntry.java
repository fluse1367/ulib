package eu.software4you.ulib.spigot.inventorymenu.entry;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an {@link Entry} which can be toggled.
 * <p>
 * The item-click will be handled by the system.
 * If you still set an own handler, this handler will be called after the item was toggled.
 * <p>Actually an simplified implementation of the {@link MultiStateEntry} with the type {@link Boolean}.
 */
public interface ToggleableEntry extends Entry {
    /**
     * Gets the toggled state of the representing item stack in the inventory.
     *
     * @return the representing item stack
     */
    @NotNull
    ItemStack getToggledRepresentation();

    /**
     * Sets the toggled state of the representing item stack.
     *
     * @param representation the item stack
     */
    void setToggledRepresentation(@NotNull ItemStack representation);

    /**
     * Gets if the entry is toggled.
     *
     * @return if the entry is toggled
     */
    boolean isToggled();

    /**
     * Sets if the entry is toggled.
     *
     * @param toggled if the entry is toggled
     */
    void setToggled(boolean toggled);
}
