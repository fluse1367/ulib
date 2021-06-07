package eu.software4you.ulib.impl.spigot.mappings;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
abstract class Mapped<T> implements eu.software4you.spigot.mappings.Mapped<T> {
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
}
