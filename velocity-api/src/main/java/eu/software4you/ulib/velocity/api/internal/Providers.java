package eu.software4you.ulib.velocity.api.internal;

import eu.software4you.ulib.velocity.api.plugin.Layout;

import java.util.ServiceLoader;

public interface Providers {
    interface ProviderLayout extends ServiceLoader.Provider<Layout> {
    }
}
