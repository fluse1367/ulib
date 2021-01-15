package eu.software4you.spigot.inventorymenu.event;

import eu.software4you.spigot.inventorymenu.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

import java.util.function.Consumer;

/**
 * @deprecated Use {@link Menu#setOpenHandler(Consumer)},
 * {@link eu.software4you.spigot.inventorymenu.factory.PageBuilder#onOpen(Consumer)},
 * {@link eu.software4you.spigot.inventorymenu.factory.MenuBuilder#onOpen(Consumer)},
 * or {@link eu.software4you.spigot.inventorymenu.factory.MultiPageMenuBuilder#onOpen(Consumer)}
 * ; Will still be called though
 */
@Deprecated
public class MenuOpenEvent extends MenuEvent {
    private static final HandlerList handlers = new HandlerList();

    public MenuOpenEvent(Player who, Menu menu) {
        super(who, menu);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
