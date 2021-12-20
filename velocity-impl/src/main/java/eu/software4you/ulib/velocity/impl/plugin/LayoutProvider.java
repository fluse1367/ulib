package eu.software4you.ulib.velocity.impl.plugin;

import eu.software4you.ulib.velocity.api.internal.Providers;
import eu.software4you.ulib.velocity.api.plugin.Layout;

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
