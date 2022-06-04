package eu.software4you.ulib.minecraft.impl;

import eu.software4you.ulib.core.util.SingletonInstance;
import eu.software4you.ulib.minecraft.plugin.PluginBase;
import eu.software4you.ulib.minecraft.util.Protocol;

public final class SharedConstants {
    // current plugin (substitute) base
    public static final SingletonInstance<PluginBase<?, ?>> BASE = new SingletonInstance<>();

    // current plain mc version
    public static final SingletonInstance<String> MC_VER = new SingletonInstance<>();

    // current protocol
    public static final SingletonInstance<Protocol> MC_PROTOCOL = new SingletonInstance<>();
}
