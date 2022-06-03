package eu.software4you.ulib.spigot.inventorymenu.menu;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Represents something that can be opened and closed (like a page) and that allows handling of these events.
 */
public interface PageHandleable {
    /**
     * Gets the handler that will be called when this page is opened.
     *
     * @return the handler that will be called when this page is opened
     */
    @Nullable
    Consumer<Player> getOpenHandler();

    /**
     * Sets the handler that will be called when this page is opened.
     *
     * @param handler the handler that will be called when this page is opened
     */
    void setOpenHandler(@Nullable Consumer<Player> handler);

    /**
     * Gets the handler that will be called when this page is closed.
     *
     * @return the handler that will be called when this page is closed
     */
    @Nullable
    Consumer<Player> getCloseHandler();

    /**
     * Sets the handler that will be called when this page is closed.
     *
     * @param handler the handler that will be called when this page is closed
     */
    void setCloseHandler(@Nullable Consumer<Player> handler);
}
