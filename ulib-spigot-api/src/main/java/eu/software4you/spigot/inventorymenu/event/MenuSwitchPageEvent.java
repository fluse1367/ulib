package eu.software4you.spigot.inventorymenu.event;

import eu.software4you.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.spigot.inventorymenu.menu.MultiPageMenu;
import eu.software4you.spigot.inventorymenu.menu.Page;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

/**
 * @deprecated Use {@link eu.software4you.spigot.inventorymenu.factory.MultiPageMenuBuilder#onPageSwitch(PageSwitchHandler)}
 * or {@link MultiPageMenu#setPageSwitchHandler(PageSwitchHandler)}
 * ; Will still be called though
 */
@Deprecated
public class MenuSwitchPageEvent extends MenuEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Page previousPage;
    private final int previousPageIndex;
    private final Page page;
    private final int pageIndex;

    public MenuSwitchPageEvent(Player who, MultiPageMenu menu, Page previousPage, int previousPageIndex, Page page, int pageIndex) {
        super(who, menu);
        this.previousPage = previousPage;
        this.previousPageIndex = previousPageIndex;
        this.page = page;
        this.pageIndex = pageIndex;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Page getPreviousPage() {
        return previousPage;
    }

    public int getPreviousPageIndex() {
        return previousPageIndex;
    }

    public Page getPage() {
        return page;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    @Override
    public MultiPageMenu getMenu() {
        return (MultiPageMenu) super.getMenu();
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
