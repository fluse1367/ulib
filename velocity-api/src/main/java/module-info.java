module ulib.velocity.api {
    requires static org.jetbrains.annotations;
    requires static lombok;

    requires ulib.core.api;
    requires ulib.minecraft.api;

    requires static com.velocitypowered.api;
    requires static net.kyori.adventure;
    requires static org.slf4j;
    requires static com.google.common;

    exports eu.software4you.ulib.velocity.api.internal to ulib.velocity, ulib.core.api;
    exports eu.software4you.ulib.velocity.api.plugin;
}