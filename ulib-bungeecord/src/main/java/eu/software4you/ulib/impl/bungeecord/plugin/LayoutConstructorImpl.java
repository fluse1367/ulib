package eu.software4you.ulib.impl.bungeecord.plugin;

import eu.software4you.bungeecord.plugin.LayoutConstructor;
import eu.software4you.ulib.inject.Impl;

@Impl(LayoutConstructor.class)
final class LayoutConstructorImpl extends LayoutConstructor {
    @Override
    protected Layout construct0() {
        return new Layout();
    }
}
