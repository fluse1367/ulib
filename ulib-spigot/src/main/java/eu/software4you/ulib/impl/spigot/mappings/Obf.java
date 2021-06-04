package eu.software4you.ulib.impl.spigot.mappings;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
abstract
class Obf<T> implements eu.software4you.spigot.mappings.Obf<T> {
    private final String name;
    private final String obfuscatedName;

    @Override
    public boolean isObfuscated() {
        return !name.equals(obfuscatedName);
    }
}
