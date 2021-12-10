package eu.software4you.ulib.impl.spigot.inventorymenu.factory;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.spigot.inventorymenu.menu.MultiPageMenu;
import eu.software4you.spigot.inventorymenu.menu.Page;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

class MultiPageMenuImpl implements MultiPageMenu {
    private final String title;
    private final Map<Integer, Page> pages;
    private ItemStack previousPageButton;
    private ItemStack nextPageButton;
    private Consumer<Player> openHandler;
    private Consumer<Player> closeHandler;
    private PageSwitchHandler pageSwitchHandler;

    MultiPageMenuImpl(String title, Map<Integer, Page> pages, ItemStack previousPageButton, ItemStack nextPageButton, Consumer<Player> openHandler, Consumer<Player> closeHandler, PageSwitchHandler pageSwitchHandler) {
        this.title = title.length() > 32 ? title.substring(0, 32) : title;
        this.pages = pages;
        this.openHandler = openHandler;
        this.closeHandler = closeHandler;
        this.pageSwitchHandler = pageSwitchHandler;
        setPageSwitchButtons(previousPageButton, nextPageButton);
    }

    @Override
    public Map<Integer, Page> getPages() {
        return Collections.unmodifiableMap(pages);
    }

    @Override
    public Page getPage(int index) {
        return pages.get(index);
    }

    @Override
    public void setPage(int index, Page page) {
        validatePageIndex(index, pages);
        if (page == null) {
            pages.remove(index);
            return;
        }
        pages.put(index, page);
        updatePageSwitchButtons(index);
        if (pages.containsKey(index - 1)) {
            updatePageSwitchButtons(index - 1);
        }
        if (pages.containsKey(index + 1)) {
            updatePageSwitchButtons(index + 1);
        }
    }

    private void updatePageSwitchButtons(int pageIndex) {
        if (!pages.containsKey(pageIndex))
            return;
        PageImpl impl = (PageImpl) pages.get(pageIndex);
        impl.setPageSwitchButtons(pages.containsKey(pageIndex - 1) ? previousPageButton : null,
                pages.containsKey(pageIndex + 1) ? nextPageButton : null);

    }

    private void validatePageIndex(int index, Map<Integer, Page> pages) {
        if (index < 0)
            throw new IndexOutOfBoundsException();
        if (index > 0 && !pages.containsKey(index - 1))
            throw new IllegalArgumentException(String.format("Cannot create page index %d with gap to another page: previous page with index %d does not exist", index, index - 1));
    }

    @Override
    public void setPageSwitchButtons(ItemStack previousPageButton, ItemStack nextPageButton) {
        this.previousPageButton = previousPageButton;
        this.nextPageButton = nextPageButton;
        pages.forEach((i, p) -> updatePageSwitchButtons(i));
    }

    @Override
    public Pair<ItemStack, ItemStack> getPageSwitchButtons() {
        return new Pair<>(previousPageButton.clone(), nextPageButton.clone());
    }

    @Override
    public void open(Player player) {
        open(player, 0);
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void open(Player player, int pageIndex) {
        getPage(pageIndex).open(player);
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

    @Override
    public PageSwitchHandler getPageSwitchHandler() {
        return pageSwitchHandler;
    }

    @Override
    public void setPageSwitchHandler(PageSwitchHandler handler) {
        this.pageSwitchHandler = handler;
    }
}
