package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.common.collection.Triple;
import eu.software4you.ulib.Loader;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class ClassMapping extends ObfClass implements eu.software4you.spigot.mappings.ClassMapping {
    private final Map<String, Loader<ObfField>> fieldsByOriginalName;
    private final Map<String, Loader<ObfField>> fieldsByObfuscatedName;
    private final Map<String, Loader<ObfMethod>> methodsByOriginalName;
    private final Map<String, Loader<ObfMethod>> methodsByObfuscatedName;

    ClassMapping(String name, String obfuscatedName,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<ObfClass, Supplier<ObfField>>>> fields,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<ObfClass, Supplier<ObfMethod>>>> methods) {
        super(name, obfuscatedName);

        Map<String, Loader<ObfField>> fieldsByOriginalName = new HashMap<>();
        Map<String, Loader<ObfField>> fieldsByObfuscatedName = new HashMap<>();
        fields.forEach(t -> {
            val loader = new Loader<>(t.getThird().apply(this));
            fieldsByOriginalName.put(t.getFirst(), loader);
            fieldsByObfuscatedName.put(t.getSecond(), loader);
        });

        Map<String, Loader<ObfMethod>> methodsByOriginalName = new HashMap<>();
        Map<String, Loader<ObfMethod>> methodsByObfuscatedName = new HashMap<>();
        methods.forEach(t -> {
            val loader = new Loader<>(t.getThird().apply(this));
            methodsByOriginalName.put(t.getFirst(), loader);
            methodsByObfuscatedName.put(t.getSecond(), loader);
        });

        this.fieldsByOriginalName = Collections.unmodifiableMap(fieldsByOriginalName);
        this.fieldsByObfuscatedName = Collections.unmodifiableMap(fieldsByObfuscatedName);
        this.methodsByOriginalName = Collections.unmodifiableMap(methodsByOriginalName);
        this.methodsByObfuscatedName = Collections.unmodifiableMap(methodsByObfuscatedName);
    }

    @Override
    public @NotNull Collection<eu.software4you.spigot.mappings.ObfField> getFields() {
        return Collections.unmodifiableCollection(fieldsByOriginalName.values().stream()
                .map(Loader::get)
                .collect(Collectors.toList()));
    }

    @Override
    public @Nullable ObfField getField(String originalName) {
        return Optional.ofNullable(fieldsByOriginalName.get(originalName)).map(Loader::get).orElse(null);
    }

    @Override
    public @Nullable ObfField searchField(String obfuscatedName) {
        return Optional.ofNullable(fieldsByObfuscatedName.get(obfuscatedName)).map(Loader::get).orElse(null);
    }

    @Override
    public @NotNull Collection<eu.software4you.spigot.mappings.ObfMethod> getMethods() {
        return Collections.unmodifiableCollection(methodsByOriginalName.values().stream()
                .map(Loader::get)
                .collect(Collectors.toList()));
    }

    @Override
    public @Nullable ObfMethod getMethod(String originalName) {
        return Optional.ofNullable(methodsByOriginalName.get(originalName)).map(Loader::get).orElse(null);
    }

    @Override
    public @Nullable ObfMethod searchMethod(String obfuscatedName) {
        return Optional.ofNullable(methodsByObfuscatedName.get(obfuscatedName)).map(Loader::get).orElse(null);
    }
}
