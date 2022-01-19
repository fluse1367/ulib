package eu.software4you.ulib.spigot.impl.plugin;

import eu.software4you.ulib.spigot.api.internal.Providers;
import eu.software4you.ulib.spigot.api.plugin.Layout;

public class LayoutProvider implements Providers.ProviderLayout {
    @Override
    public Class<? extends Layout> type() {
        return LayoutImpl.class;
    }

    @Override
    public Layout get() {
        return new LayoutImpl();
    }
}
