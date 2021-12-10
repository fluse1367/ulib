package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.ulib.core.api.reflect.ReflectUtil;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

class MappedClass extends Mapped<Class<?>> implements eu.software4you.spigot.mappings.MappedClass {
    MappedClass(String sourceName, String mappedName) {
        super(sourceName, mappedName);
    }

    @SneakyThrows
    @Override
    public @NotNull Class<?> find() {
        return Class.forName(mappedName(), false, ReflectUtil.getCallerClass().getClassLoader());
    }
}
