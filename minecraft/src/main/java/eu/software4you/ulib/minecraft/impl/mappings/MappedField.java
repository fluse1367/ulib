package eu.software4you.ulib.minecraft.impl.mappings;

import eu.software4you.ulib.core.util.Expect;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

final class MappedField extends Mapped<Field> implements eu.software4you.ulib.minecraft.mappings.MappedField {
    private final MappedClass parent;
    private final MappedClass type;

    MappedField(MappedClass parent, MappedClass type, String sourceName, String mappedName) {
        super(sourceName, mappedName);
        this.parent = parent;
        this.type = type;
    }

    @Override
    @NotNull
    public MappedClass type() {
        return type;
    }

    @SneakyThrows
    @Override
    public @NotNull Expect<Field, ?> find() {
        return parent.find()
                .map(cl -> cl.getDeclaredField(mappedName()));
    }
}
