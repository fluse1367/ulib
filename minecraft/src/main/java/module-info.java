module ulib.minecraft {
    // static
    requires static lombok;
    requires static org.jetbrains.annotations;

    // java
    requires java.sql;

    // 3rd party

    // ulib
    requires ulib.core;

    // api exports
    exports eu.software4you.ulib.minecraft.launchermeta;
    exports eu.software4you.ulib.minecraft.plugin.controllers;
    exports eu.software4you.ulib.minecraft.plugin;
    exports eu.software4you.ulib.minecraft.proxybridge.command;
    exports eu.software4you.ulib.minecraft.proxybridge.message;
    exports eu.software4you.ulib.minecraft.proxybridge;
    exports eu.software4you.ulib.minecraft.usercache;
    exports eu.software4you.ulib.minecraft.util;
}