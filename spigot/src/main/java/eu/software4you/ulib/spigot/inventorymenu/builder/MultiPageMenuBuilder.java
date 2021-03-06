package eu.software4you.ulib.spigot.inventorymenu.builder;

import com.cryptomorin.xseries.XMaterial;
import eu.software4you.ulib.spigot.impl.inventorymenu.MultiPageMenuImpl;
import eu.software4you.ulib.spigot.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.ulib.spigot.inventorymenu.menu.MultiPageMenu;
import eu.software4you.ulib.spigot.inventorymenu.menu.Page;
import eu.software4you.ulib.spigot.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * A builder for a {@link MultiPageMenu}.
 */
public class MultiPageMenuBuilder {
    private final String title;
    private final Map<Integer, Page> pages = new HashMap<>();
    private ItemStack previousPageButton;
    private ItemStack nextPageButton;
    private Consumer<Player> openHandler = null;
    private Consumer<Player> closeHandler = null;
    private PageSwitchHandler pageSwitchHandler = null;

    public MultiPageMenuBuilder(@NotNull String title) {
        this.title = title;
        ItemBuilder b = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial());
        SkullMeta meta = b.getMeta(SkullMeta.class).orElseThrow();
        meta.setOwner("MHF_ArrowLeft");
        previousPageButton = b.name("§e§l<-").build();
        meta.setOwner("MHF_ArrowRight");
        nextPageButton = b.name("§e§l->").build();
    }

    private void validatePageIndex(int index, Map<Integer, Page> pages) {
        if (index < 0)
            throw new IndexOutOfBoundsException();
        if (index > 0 && !pages.containsKey(index - 1))
            throw new IllegalArgumentException(String.format("Cannot create page index %d with gap to another page: previous page with index %d does not exist", index, index - 1));
    }

    /**
     * Adds a page to the next available index.
     *
     * @param page the page to add
     * @return this
     * @see MultiPageMenu#setPage(int, Page)
     */
    @NotNull
    @Contract("_ -> this")
    public MultiPageMenuBuilder addPage(@NotNull Page page) {
        setPage(pages.size(), page);
        return this;
    }


    /**
     * Adds multiple pages to the next available indexes.
     *
     * @param page  the page to add
     * @param pages other pages to add
     * @return this
     * @see MultiPageMenu#setPage(int, Page)
     */
    @NotNull
    @Contract("_, _ -> this")
    public MultiPageMenuBuilder addPages(@NotNull Page page, @NotNull Page... pages) {
        addPage(page);
        for (Page pg : pages) {
            addPage(pg);
        }
        return this;
    }

    /**
     * @see MultiPageMenu#setPage(int, Page)
     */
    @NotNull
    @Contract("_, _ -> this")
    public MultiPageMenuBuilder setPage(int index, @NotNull Page page) {
        validatePageIndex(index, pages);
        pages.put(index, page);
        return this;
    }

    /**
     * @see MultiPageMenu#setPageSwitchButtons(ItemStack, ItemStack)
     */
    @NotNull
    @Contract("_, _ -> this")
    public MultiPageMenuBuilder setPageSwitchButtons(@Nullable ItemStack previousPageButton, @Nullable ItemStack nextPageButton) {
        this.previousPageButton = previousPageButton;
        this.nextPageButton = nextPageButton;
        return this;
    }

    /**
     * @see MultiPageMenu#setOpenHandler(Consumer)
     */
    @NotNull
    @Contract("_ -> this")
    public MultiPageMenuBuilder onOpen(@NotNull Consumer<Player> handler) {
        this.openHandler = handler;
        return this;
    }

    /**
     * @see MultiPageMenu#setCloseHandler(Consumer)
     */
    @NotNull
    @Contract("_ -> this")
    public MultiPageMenuBuilder onClose(@NotNull Consumer<Player> handler) {
        this.closeHandler = handler;
        return this;
    }

    /**
     * @see MultiPageMenu#setPageSwitchHandler(PageSwitchHandler)
     */
    @NotNull
    @Contract("_ -> this")
    public MultiPageMenuBuilder onPageSwitch(@NotNull PageSwitchHandler handler) {
        this.pageSwitchHandler = handler;
        return this;
    }

    @NotNull
    @Contract("-> new")
    public MultiPageMenu build() {
        return new MultiPageMenuImpl(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }
}
