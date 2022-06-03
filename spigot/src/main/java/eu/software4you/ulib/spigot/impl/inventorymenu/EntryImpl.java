package eu.software4you.ulib.spigot.impl.inventorymenu;

import eu.software4you.ulib.spigot.inventorymenu.entry.Entry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.BiConsumer;

public class EntryImpl implements Entry {
    private PageImpl parent = null;
    private int slot = -1;
    private ItemStack representation;
    private String clickPermission;
    private BiConsumer<Player, ClickType> clickHandler;

    public EntryImpl(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        this.representation = representation;
        this.clickPermission = clickPermission;
        this.clickHandler = clickHandler;
    }

    @Override
    public @NotNull ItemStack getRepresentation() {
        if (representation == null)
            return null; // TODO: throw error?
        return representation.clone();
    }

    @Override
    public void setRepresentation(ItemStack representation) {
        this.representation = representation.clone();
        if (parent != null) {
            parent.updateSlot(slot);
        }
    }

    @Override
    public @NotNull String getClickPermission() {
        return clickPermission;
    }

    @Override
    public void setClickPermission(String permission) {
        clickPermission = Objects.requireNonNullElse(permission, "");
    }

    PageImpl getParent() {
        return this.parent;
    }

    void setParent(PageImpl parent) {
        this.parent = parent;
    }

    int getSlot() {
        return slot;
    }

    void setSlot(int slot) {
        this.slot = slot;
    }

    @Override
    public BiConsumer<Player, ClickType> getClickHandler() {
        return clickHandler;
    }

    @Override
    public void setClickHandler(BiConsumer<Player, ClickType> handler) {
        this.clickHandler = handler;
    }
}
