package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.inventorymenu.entry.ToggleableEntry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class ToggleableEntryImpl extends MultiStateEntryImpl<Boolean> implements ToggleableEntry {

    public ToggleableEntryImpl(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler) {
        super(false, representation, new HashMap<>(), new HashMap<>(), handler, clickPermission);
        setRepresentation(true, toggledRepresentation);
    }

    @Override
    public ItemStack getToggledRepresentation() {
        return super.getRepresentation(true);
    }

    @Override
    public void setToggledRepresentation(ItemStack representation) {
        setRepresentation(true, representation);
    }

    @Override
    public boolean isToggled() {
        return getState();
    }

    @Override
    public void setToggled(boolean toggled) {
        setState(toggled);
    }
}
