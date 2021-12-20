package eu.software4you.ulib.velocity.api.internal;

import eu.software4you.ulib.velocity.api.plugin.Layout;

import java.util.ServiceLoader;

public final class Providers {
    public interface ProviderLayout extends ServiceLoader.Provider<Layout> {
    }
}
