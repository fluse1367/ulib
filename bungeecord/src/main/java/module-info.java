module ulib.bungeecord {
    // static
    requires static lombok;
    requires static org.jetbrains.annotations;
    requires bungeecord.api;

    // java
    requires java.logging;

    // 3rd party

    // ulib
    requires transitive ulib.minecraft;

    // api exports
    exports eu.software4you.ulib.bungeecord.player;
    exports eu.software4you.ulib.bungeecord.plugin;

    // impl exports
    exports eu.software4you.ulib.bungeecord.impl to ulib.loader;
}