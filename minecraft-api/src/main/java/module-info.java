module ulib.minecraft.api {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core.api;
    requires java.logging;

    exports eu.software4you.ulib.minecraft.api.launchermeta;
    exports eu.software4you.ulib.minecraft.api.plugin.controllers;
    exports eu.software4you.ulib.minecraft.api.plugin;
    exports eu.software4you.ulib.minecraft.api.proxybridge.command;
    exports eu.software4you.ulib.minecraft.api.proxybridge.message;
    exports eu.software4you.ulib.minecraft.api.proxybridge;
    exports eu.software4you.ulib.minecraft.api.usercache;
    exports eu.software4you.ulib.minecraft.api.util;

    exports eu.software4you.ulib.minecraft.api.internal to ulib.velocity;
}