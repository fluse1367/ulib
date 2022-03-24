module ulib.loader {
    // only compile time
    requires static lombok;

    // loaded later
    requires static ulib.core;

    // for launch function
    requires static joptsimple;

    // minecraft
    requires static bungeecord.api; // bungeecord
    requires static org.bukkit; // spigot
    requires static com.velocitypowered.api; // velocity
    requires static fabric.loader; // fabric
    requires static org.slf4j;
    requires static com.google.guice;

    // java
    requires java.instrument;
    requires java.logging;
    requires jdk.attach;

    exports eu.software4you.ulib.loader.install;
    exports eu.software4you.ulib.loader.agent to java.instrument;
}