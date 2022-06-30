module ulib.minecraft {
    // static
    requires static lombok;
    requires static org.jetbrains.annotations;

    // java
    requires java.sql;

    // 3rd party

    // ulib
    requires transitive ulib.core;
    requires java.net.http;

    // api exports
    exports eu.software4you.ulib.minecraft.launchermeta;
    exports eu.software4you.ulib.minecraft.mappings;
    exports eu.software4you.ulib.minecraft.plugin.controllers;
    exports eu.software4you.ulib.minecraft.plugin;
    exports eu.software4you.ulib.minecraft.proxybridge.command;
    exports eu.software4you.ulib.minecraft.proxybridge.message;
    exports eu.software4you.ulib.minecraft.proxybridge;
    exports eu.software4you.ulib.minecraft.usercache;
    exports eu.software4you.ulib.minecraft.util;

    // impl exports
    exports eu.software4you.ulib.minecraft.impl to ulib.velocity, ulib.bungeecord, ulib.spigot;
    exports eu.software4you.ulib.minecraft.impl.proxybridge to ulib.velocity, ulib.bungeecord, ulib.spigot;
    exports eu.software4you.ulib.minecraft.impl.usercache to ulib.velocity, ulib.bungeecord, ulib.spigot;
    exports eu.software4you.ulib.minecraft.impl.launchermeta to ulib.spigot;
}