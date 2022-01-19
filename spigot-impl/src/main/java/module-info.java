module ulib.spigot {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core;
    requires ulib.core.api;
    requires ulib.spigot.api;
    requires ulib.minecraft;
    requires ulib.minecraft.api;

    requires org.yaml.snakeyaml;
    requires org.bukkit;
    requires com.google.gson;
    requires com.google.common;
    requires java.logging;

    // via extra-java-module-info
    requires xseries;
}