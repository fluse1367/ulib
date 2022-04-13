module ulib.loader {
    // static
    requires static lombok;

    // ulib
    requires static ulib.core; // static bc it's loaded later

    // 3rd party
    requires static org.slf4j;
    requires static com.google.guice;
    requires static joptsimple;

    // minecraft; static bc loader won't get loaded as module when in minecraft context
    requires static bungeecord.api; // bungeecord
    requires static org.bukkit; // spigot
    requires static com.velocitypowered.api; // velocity
    requires static fabric.loader; // fabric

    // java
    requires java.instrument;
    requires java.logging;
    requires jdk.attach;

    // api exports
    exports eu.software4you.ulib.loader.install;
    exports eu.software4you.ulib.loader.agent to java.instrument;
}