module ulib.spigot {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core;
    requires ulib.core.api;
    requires ulib.spigot.api;
    requires ulib.minecraft;
    requires ulib.minecraft.api;

    requires org.yaml.snakeyaml;
    requires java.logging;
    requires static org.bukkit;
    requires static com.google.gson;
    requires static com.google.common;

    // via extra-java-module-info
    requires xseries;

    opens eu.software4you.ulib.spigot.impl;
}