package eu.software4you.ulib.spigot.inventorymenu.builder;

import eu.software4you.ulib.spigot.impl.inventorymenu.ToggleableEntryImpl;
import eu.software4you.ulib.spigot.inventorymenu.entry.ToggleableEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;

/**
 * A builder for a  {@link ToggleableEntry}.
 */
public class ToggleableEntryBuilder extends EntryBuilder {
    private final ItemStack toggledRepresentation;

    public ToggleableEntryBuilder(@NotNull ItemStack representation, @NotNull ItemStack toggledRepresentation) {
        super(representation);
        this.toggledRepresentation = toggledRepresentation;
    }

    /**
     * @see ToggleableEntry#setClickPermission(String)
     */
    @Override
    @Contract("_ -> this")
    public @NotNull ToggleableEntryBuilder clickPermission(@NotNull String clickPermission) {
        super.clickPermission(clickPermission);
        return this;
    }

    /**
     * @see ToggleableEntry#setClickHandler(BiConsumer)
     */
    @Override
    @Contract("_ -> this")
    public @NotNull ToggleableEntryBuilder onClick(@NotNull BiConsumer<Player, ClickType> handler) {
        super.onClick(handler);
        return this;
    }

    @Contract("-> new")
    public @NotNull ToggleableEntry build() {
        return new ToggleableEntryImpl(representation, toggledRepresentation, clickPermission, onClick);
    }
}
