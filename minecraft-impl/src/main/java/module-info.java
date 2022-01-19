import eu.software4you.ulib.minecraft.api.launchermeta.LauncherMeta;
import eu.software4you.ulib.minecraft.impl.launchermeta.LauncherMetaImpl;

module ulib.minecraft {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires java.sql;

    requires com.google.gson;
    requires org.yaml.snakeyaml;

    requires ulib.minecraft.api;
    requires ulib.core.api;
    requires ulib.core;

    exports eu.software4you.ulib.minecraft.impl.usercache to ulib.velocity, ulib.spigot;
    exports eu.software4you.ulib.minecraft.impl.proxybridge to ulib.velocity, ulib.spigot;
    exports eu.software4you.ulib.minecraft.impl.plugin to ulib.velocity, ulib.spigot;

    provides LauncherMeta with LauncherMetaImpl;
}