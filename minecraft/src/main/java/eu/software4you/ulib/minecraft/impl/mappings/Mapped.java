package eu.software4you.ulib.minecraft.impl.mappings;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
abstract class Mapped<T> implements eu.software4you.ulib.minecraft.mappings.Mapped<T> {
    private final String sourceName;
    private final String mappedName;

    @Override
    public boolean mapped() {
        return !sourceName.equals(mappedName);
    }

    @Override
    @NotNull
    public String sourceName() {
        return sourceName;
    }

    @Override
    @NotNull
    public String mappedName() {
        return mappedName;
    }

    @Override
    public String toString() {
        return "{[%s@%x] %s -> %s}".formatted(getClass().getSimpleName(), hashCode(),
                sourceName, mappedName);
    }
}
