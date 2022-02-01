package eu.software4you.ulib.impl.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.menu.Page;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

class PageImpl implements Page {
    private final Inventory inventory;
    private final String title;
    private final int rows;
    private final Map<Integer, Entry> entries;
    private ItemStack previousPageButton;
    private ItemStack nextPageButton;
    private Consumer<Player> openHandler;
    private Consumer<Player> closeHandler;

    PageImpl(String title, int rows, Map<Integer, Entry> entries, Consumer<Player> openHandler, Consumer<Player> closeHandler) {
        if (rows <= 0)
            throw new IllegalArgumentException("Row count must be greater than 0");
        this.rows = rows;
        this.title = title.length() > 32 ? title.substring(0, 32) : title;
        this.inventory = Bukkit.createInventory(null, rows * 9, this.title);
        this.entries = entries;
        this.openHandler = openHandler;
        this.closeHandler = closeHandler;
        for (Map.Entry<Integer, Entry> en : entries.entrySet()) {
            EntryImpl entry = (EntryImpl) en.getValue();
            entry.setParent(this);
            entry.setSlot(en.getKey());
        }
        for (int i = 0; i < rows * 9; i++) {
            updateSlot(i);
        }
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public int getRows() {
        return this.rows;
    }

    @Override
    public Map<Integer, Entry> getEntries() {
        return Collections.unmodifiableMap(entries);
    }

    @Override
    public void setEntry(int slot, Entry entry) {
        validateSlot(slot);
        EntryImpl impl;
        if (entry == null) {
            if (entries.containsKey(slot)) {
                impl = (EntryImpl) entries.get(slot);
                impl.setParent(null);
                impl.setSlot(-1);
                entries.remove(slot);
            }
            return;
        }
        impl = (EntryImpl) entry;
        if (impl.getParent() != null)
            throw new IllegalStateException("Entry belongs to another page");
        impl.setParent(this);
        impl.setSlot(slot);
        entries.put(slot, entry);
        updateSlot(slot);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @Override
    public void open(Player player) {
        player.openInventory(inventory);
    }

    @Override
    public Consumer<Player> getOpenHandler() {
        return openHandler;
    }

    @Override
    public void setOpenHandler(Consumer<Player> handler) {
        this.openHandler = handler;
    }

    @Override
    public Consumer<Player> getCloseHandler() {
        return closeHandler;
    }

    @Override
    public void setCloseHandler(Consumer<Player> handler) {
        this.closeHandler = handler;
    }

    private void validateSlot(int slot) {
        if (previousPageButton != null && slot == rows * 9 - 9
            || nextPageButton != null && slot == rows * 9 - 1)
            throw new IllegalArgumentException(String.format("Slot %d reserved for page switch button", slot));
    }

    void setPageSwitchButtons(ItemStack previousPageButton, ItemStack nextPageButton) {
        this.previousPageButton = previousPageButton;
        this.nextPageButton = nextPageButton;
        inventory.setItem(rows * 9 - 9, this.previousPageButton);
        inventory.setItem(rows * 9 - 1, this.nextPageButton);
    }

    void updateSlot(int slot) {
        if (!entries.containsKey(slot))
            return;
        validateSlot(slot);
        ItemStack representation = entries.get(slot).getRepresentation();
        ItemStack stack = inventory.getItem(slot);
        if (stack != null && stack.isSimilar(representation))
            return;
        inventory.setItem(slot, representation);
    }
}
