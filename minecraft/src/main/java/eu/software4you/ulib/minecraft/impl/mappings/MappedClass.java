package eu.software4you.ulib.minecraft.impl.mappings;

import eu.software4you.ulib.core.reflect.ReflectUtil;
import eu.software4you.ulib.core.util.Conversions;
import eu.software4you.ulib.core.util.Expect;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

class MappedClass extends Mapped<Class<?>> implements eu.software4you.ulib.minecraft.mappings.MappedClass {
    MappedClass(String sourceName, String mappedName) {
        super(sourceName, mappedName);
    }

    @SneakyThrows
    @Override
    public @NotNull Expect<Class<?>, ?> find() {
        return Conversions.tryWithLoaders(l -> ReflectUtil.forName(mappedName(), false, l).orElseRethrow());
    }
}
