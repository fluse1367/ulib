package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.reflect.ReflectUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

class ObfClass extends Obf<Class<?>> implements eu.software4you.spigot.mappings.ObfClass {
    ObfClass(String name, String obfuscatedName) {
        super(name, obfuscatedName);
    }

    @SneakyThrows
    @Override
    public @NotNull Class<?> find() {
        return Class.forName(getObfuscatedName(), false, ReflectUtil.getCallerClass().getClassLoader());
    }
}
