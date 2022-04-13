module ulib.velocity {
    // static
    requires static lombok;
    requires static org.jetbrains.annotations;
    requires static com.google.common;
    requires static com.google.gson;
    requires static com.velocitypowered.api;

    // java

    // 3rd party

    // ulib
    requires transitive ulib.minecraft;

    // api exports
    exports eu.software4you.ulib.velocity.plugin;

    // impl exports
    exports eu.software4you.ulib.velocity.impl to ulib.loader;
}