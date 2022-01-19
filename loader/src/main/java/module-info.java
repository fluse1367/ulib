module ulib.loader {
    // only compile time
    requires static lombok;

    // loaded later
    requires static ulib.core.api;
    requires static ulib.core;
    requires static ulib.velocity;
    requires static ulib.spigot;

    // for launch function
    requires static joptsimple;

    // minecraft
    requires static bungeecord.api; // bungeecord
    requires static org.bukkit; // spigot
    requires static com.velocitypowered.api; // velocity
    requires static org.slf4j;
    requires static com.google.guice;

    requires java.instrument;
    requires java.logging;
    requires jdk.attach;

    exports eu.software4you.ulib.loader.install;
    exports eu.software4you.ulib.loader.agent to java.instrument;
}