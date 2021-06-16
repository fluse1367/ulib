package eu.software4you.ulib.impl.velocity.plugin;

import eu.software4you.ulib.inject.Impl;
import eu.software4you.velocity.plugin.LayoutConstructor;

@Impl(LayoutConstructor.class)
final class LayoutConstructorImpl extends LayoutConstructor {
    @Override
    protected Layout construct0() {
        return new Layout();
    }
}
