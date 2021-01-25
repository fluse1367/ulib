package eu.software4you.ulib.impl.spigot.inventorymenu.factory;

import eu.software4you.spigot.inventorymenu.entry.Entry;
import eu.software4you.spigot.inventorymenu.entry.MultiStateEntry;
import eu.software4you.spigot.inventorymenu.entry.ToggleableEntry;
import eu.software4you.spigot.inventorymenu.factory.EntryFactory;
import eu.software4you.ulib.ImplRegistry;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.function.BiConsumer;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EntryFactoryImpl extends EntryFactory {

    static {
        ImplRegistry.put(EntryFactory.class, new EntryFactoryImpl());
    }

    @Override
    protected Entry implCreateEntry(ItemStack representation, String clickPermission, BiConsumer<Player, ClickType> clickHandler) {
        return new EntryImpl(representation, clickPermission, clickHandler);
    }

    @Override
    protected <T> MultiStateEntry<T> implCreateMultiStateEntry(T defaultState, ItemStack representation, Map<T, ItemStack> representations, Map<T, BiConsumer<Player, ClickType>> handlers, BiConsumer<Player, ClickType> defaultClickHandler, String clickPermission) {
        return new MultiStateEntryImpl<>(defaultState, representation, representations, handlers, defaultClickHandler, clickPermission);
    }

    @Override
    protected ToggleableEntry implCreateToggleableEntry(ItemStack representation, ItemStack toggledRepresentation, String clickPermission, BiConsumer<Player, ClickType> handler) {
        return new ToggleableEntryImpl(representation, toggledRepresentation, clickPermission, handler);
    }
}
