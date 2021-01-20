package eu.software4you.spigot.inventorymenu;

import org.bukkit.event.Listener;

import java.util.function.Function;

public class MenuManagerInit {
    public static void menuManager(Function<MenuManager, ? extends Listener> handlerFunction) {
        MenuManager.handlerFunction = (Function<MenuManager, MenuManager.Handler>) handlerFunction;
    }
}
