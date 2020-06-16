package eu.software4you.minecraft.inventorymenu.factory;

import com.cryptomorin.xseries.XMaterial;
import eu.software4you.minecraft.inventorymenu.handlers.PageSwitchHandler;
import eu.software4you.minecraft.inventorymenu.menu.MultiPageMenu;
import eu.software4you.minecraft.inventorymenu.menu.Page;
import eu.software4you.minecraft.item.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MultiPageMenuBuilder {
    private final String title;
    private final Map<Integer, Page> pages = new HashMap<>();
    private ItemStack previousPageButton;
    private ItemStack nextPageButton;
    private Consumer<Player> openHandler = null;
    private Consumer<Player> closeHandler = null;
    private PageSwitchHandler pageSwitchHandler = null;

    public MultiPageMenuBuilder(String title) {
        this.title = title;
        ItemBuilder b = new ItemBuilder(XMaterial.PLAYER_HEAD.parseMaterial());
        SkullMeta meta = b.getMeta(SkullMeta.class);
        meta.setOwner("MHF_ArrowLeft");
        previousPageButton = b.name("§e§l<-").build();
        meta.setOwner("MHF_ArrowRight");
        nextPageButton = b.name("§e§l->").build();
    }

    public MultiPageMenuBuilder addPage(Page page) {
        setPage(pages.size(), page);
        return this;
    }

    public MultiPageMenuBuilder addPages(Page page, Page... pages) {
        addPage(page);
        for (Page pg : pages) {
            addPage(pg);
        }
        return this;
    }

    public MultiPageMenuBuilder setPage(int index, Page page) {
        MultiPageMenuImpl.validatePageIndex(index, pages);
        pages.put(index, page);
        return this;
    }

    public MultiPageMenuBuilder setPageSwitchButtons(ItemStack previousPageButton, ItemStack nextPageButton) {
        this.previousPageButton = previousPageButton;
        this.nextPageButton = nextPageButton;
        return this;
    }

    public MultiPageMenuBuilder onOpen(Consumer<Player> handler) {
        this.openHandler = handler;
        return this;
    }

    public MultiPageMenuBuilder onClose(Consumer<Player> handler) {
        this.closeHandler = handler;
        return this;
    }

    public MultiPageMenuBuilder onPageSwitch(PageSwitchHandler handler) {
        this.pageSwitchHandler = handler;
        return this;
    }

    public MultiPageMenu build() {
        return MenuFactory.createMultiPageMenu(title, pages, previousPageButton, nextPageButton, openHandler, closeHandler, pageSwitchHandler);
    }
}
