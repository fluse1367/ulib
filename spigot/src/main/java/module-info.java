module ulib.spigot {
    // static
    requires static org.jetbrains.annotations;
    requires static lombok;
    requires static org.bukkit;
    requires static com.google.common;
    requires static com.google.gson;
    requires static commons.lang;

    // java
    requires java.logging;

    // 3rd party
    requires xseries;

    // ulib
    requires transitive ulib.minecraft;

    // api exports
    exports eu.software4you.ulib.spigot.enchantment;
    exports eu.software4you.ulib.spigot.inventorymenu.builder;
    exports eu.software4you.ulib.spigot.inventorymenu.handlers;
    exports eu.software4you.ulib.spigot.inventorymenu.menu;
    exports eu.software4you.ulib.spigot.inventorymenu;
    exports eu.software4you.ulib.spigot.item;
    exports eu.software4you.ulib.spigot.mappings;
    exports eu.software4you.ulib.spigot.plugin;
    exports eu.software4you.ulib.spigot.util;

    // impl exports
    exports eu.software4you.ulib.spigot.impl to ulib.loader;
}