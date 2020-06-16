package eu.software4you.minecraft.inventorymenu.event;

import eu.software4you.minecraft.inventorymenu.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

@Deprecated
public abstract class MenuEvent extends PlayerEvent {
    private final Menu menu;

    public MenuEvent(Player who, Menu menu) {
        super(who);
        this.menu = menu;
    }

    public Menu getMenu() {
        return menu;
    }
}
