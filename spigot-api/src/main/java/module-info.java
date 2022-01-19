module ulib.spigot.api {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core.api;
    requires ulib.minecraft.api;

    requires java.logging;
    requires static org.bukkit;
    requires static com.google.common;

    // via extra-java-module-info
    requires xseries;
    requires static commons.lang;

    exports eu.software4you.ulib.spigot.api.internal to ulib.spigot;

    exports eu.software4you.ulib.spigot.api;
    exports eu.software4you.ulib.spigot.api.enchantment;
    exports eu.software4you.ulib.spigot.api.inventorymenu;
    exports eu.software4you.ulib.spigot.api.inventorymenu.builder;
    exports eu.software4you.ulib.spigot.api.inventorymenu.entry;
    exports eu.software4you.ulib.spigot.api.inventorymenu.handlers;
    exports eu.software4you.ulib.spigot.api.inventorymenu.menu;
    exports eu.software4you.ulib.spigot.api.item;
    exports eu.software4you.ulib.spigot.api.mappings;
    exports eu.software4you.ulib.spigot.api.multiversion;
    exports eu.software4you.ulib.spigot.api.plugin;
}