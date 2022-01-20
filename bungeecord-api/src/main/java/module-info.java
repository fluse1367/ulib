module ulib.bungeecord.api {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core.api;
    requires ulib.minecraft.api;

    requires static bungeecord.api;
    requires static bungeecord.chat;
    requires java.logging;

    exports eu.software4you.ulib.bungeecord.api.internal to ulib.bungeecord;

    exports eu.software4you.ulib.bungeecord.api.plugin;
    exports eu.software4you.ulib.bungeecord.api.player;
}