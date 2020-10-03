package eu.software4you.minecraft.inventorymenu.factory;

import eu.software4you.minecraft.inventorymenu.entry.Entry;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

class EntryImpl implements Entry {
    private PageImpl parent = null;
    private int slot = -1;
    private ItemStack representation;
    private String clickPermission;
    private BiConsumer<Player, ClickType> clickHandler;

    EntryImpl(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        this.representation = representation;
        this.clickPermission = clickPermission;
        this.clickHandler = clickHandler;
    }

    @Override
    public ItemStack getRepresentation() {
        if (representation == null)
            return null;
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
    public String getClickPermission() {
        return clickPermission;
    }

    @Override
    public void setClickPermission(String permission) {
        clickPermission = permission;
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
