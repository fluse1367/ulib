package eu.software4you.ulib.spigot.impl;

import org.bukkit.event.*;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;

public class DelegationListener implements Listener {


    public static <T extends Event> Listener registerSingleDelegation(Class<T> clazz, Consumer<T> consumer, boolean ignoreCancelled) {
        return registerSingleDelegation(clazz, consumer, EventPriority.NORMAL, ignoreCancelled);
    }

    public static <T extends Event> Listener registerSingleDelegation(Class<T> clazz, Consumer<T> consumer) {
        return registerSingleDelegation(clazz, consumer, EventPriority.NORMAL);
    }

    public static <T extends Event> Listener registerSingleDelegation(Class<T> clazz, Consumer<T> consumer, EventPriority priority) {
        return registerSingleDelegation(clazz, consumer, priority, false);
    }

    public static <T extends Event> Listener registerSingleDelegation(Class<T> clazz, Consumer<T> consumer, EventPriority priority, boolean ignoreCancelled) {
        var delegate = new DelegationListener();
        registerDelegation(delegate, clazz, consumer, priority, ignoreCancelled);
        return delegate;
    }

    public static <T extends Event> void registerDelegation(Listener delegation, Class<T> clazz, Consumer<T> consumer, boolean ignoreCancelled) {
        registerDelegation(delegation, clazz, consumer, EventPriority.NORMAL, ignoreCancelled);
    }

    public static <T extends Event> void registerDelegation(Listener delegation, Class<T> clazz, Consumer<T> consumer) {
        registerDelegation(delegation, clazz, consumer, EventPriority.NORMAL);
    }

    public static <T extends Event> void registerDelegation(Listener delegation, Class<T> clazz, Consumer<T> consumer, EventPriority priority) {
        registerDelegation(delegation, clazz, consumer, priority, false);
    }

    public static <T extends Event> void registerDelegation(Listener delegation, Class<T> clazz, Consumer<T> consumer, EventPriority priority, boolean ignoreCancelled) {
        registerDelegation(delegation, clazz, consumer, priority, ignoreCancelled, PluginSubst.getInstance().getPluginObject());
    }

    public static <T extends Event> void registerDelegation(Listener delegation, Class<T> clazz, Consumer<T> consumer, EventPriority priority, boolean ignoreCancelled, Plugin pl) {
        //noinspection unchecked
        pl.getServer().getPluginManager().registerEvent(clazz, delegation, priority,
                (listener, event) -> consumer.accept((T) event), pl, ignoreCancelled);
    }

}
