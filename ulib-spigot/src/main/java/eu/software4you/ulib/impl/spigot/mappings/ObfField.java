package eu.software4you.ulib.impl.spigot.mappings;

import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

final class ObfField extends Obf<Field> implements eu.software4you.spigot.mappings.ObfField {
    private final ObfClass parent;
    private final ObfClass type;

    ObfField(ObfClass parent, ObfClass type, String name, String obfuscatedName) {
        super(name, obfuscatedName);
        this.parent = parent;
        this.type = type;
    }

    @Override
    @NotNull
    public ObfClass getType() {
        return type;
    }

    @SneakyThrows
    @Override
    public @NotNull Field find() {
        return parent.find().getDeclaredField(getObfuscatedName());
    }
}
