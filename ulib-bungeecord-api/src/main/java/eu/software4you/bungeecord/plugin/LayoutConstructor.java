package eu.software4you.bungeecord.plugin;

import eu.software4you.ulib.Await;

public abstract class LayoutConstructor {
    @Await
    private static LayoutConstructor impl;

    static Layout construct() {
        return impl.construct0();
    }

    protected abstract Layout construct0();
}
