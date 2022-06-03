package eu.software4you.ulib.spigot.inventorymenu.entry;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Represents a simple entry within a {@link eu.software4you.ulib.spigot.inventorymenu.menu.Page} displayed with an {@link ItemStack}.
 *
 * @see eu.software4you.ulib.spigot.inventorymenu.builder.EntryBuilder
 */
public interface Entry {
    /**
     * Gets the representing item stack in the inventory.
     *
     * @return a copy of the representing item stack
     */
    @NotNull
    ItemStack getRepresentation();

    /**
     * Sets the representing item stack.
     *
     * @param representation the item stack
     */
    void setRepresentation(@NotNull ItemStack representation);

    /**
     * Gets the permission a player needs in order to click this entry.
     *
     * @return the permission
     */
    @NotNull
    String getClickPermission();

    /**
     * Sets the permission a player needs in order to click this entry.
     *
     * @param permission the permission to set, {@code null} or an empty string if no permission is required
     */
    void setClickPermission(@Nullable String permission);

    /**
     * Gets the handler that will be called on a successful click from a player.
     *
     * @return the handler that will be called on a successful click from a player
     */
    @Nullable
    BiConsumer<Player, ClickType> getClickHandler();

    /**
     * Sets the handler that will be called on a successful click from a player.
     *
     * @param handler the handler that will be called on a successful click from a player
     */
    void setClickHandler(@Nullable BiConsumer<Player, ClickType> handler);
}
