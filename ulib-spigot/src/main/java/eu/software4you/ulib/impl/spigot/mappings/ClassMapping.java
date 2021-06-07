package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.common.collection.Triple;
import eu.software4you.spigot.mappings.MappedField;
import eu.software4you.spigot.mappings.MappedMethod;
import eu.software4you.ulib.Loader;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class ClassMapping extends MappedClass implements eu.software4you.spigot.mappings.ClassMapping {
    private final Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsBySourceName;
    private final Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsByMappedName;
    private final Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>> methodsBySourceName;
    private final Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>> methodsByMappedName;

    ClassMapping(String sourceName, String mappedName,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<MappedClass, Supplier<eu.software4you.ulib.impl.spigot.mappings.MappedField>>>> fields,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<MappedClass, Supplier<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>>> methods) {
        super(sourceName, mappedName);

        Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsByOriginalName = new HashMap<>();
        Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsByMappedName = new HashMap<>();
        fields.forEach(t -> {
            val loader = new Loader<>(t.getThird().apply(this));
            fieldsByOriginalName.put(t.getFirst(), loader);
            fieldsByMappedName.put(t.getSecond(), loader);
        });

        Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>> methodsByOriginalName = new HashMap<>();
        Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>> methodsByMappedName = new HashMap<>();
        methods.forEach(t -> {
            val loader = new Loader<>(t.getThird().apply(this));
            methodsByOriginalName.put(t.getFirst(), loader);
            methodsByMappedName.put(t.getSecond(), loader);
        });

        this.fieldsBySourceName = Collections.unmodifiableMap(fieldsByOriginalName);
        this.fieldsByMappedName = Collections.unmodifiableMap(fieldsByMappedName);
        this.methodsBySourceName = Collections.unmodifiableMap(methodsByOriginalName);
        this.methodsByMappedName = Collections.unmodifiableMap(methodsByMappedName);
    }

    @Override
    public @NotNull Collection<MappedField> fields() {
        return Collections.unmodifiableCollection(fieldsBySourceName.values().stream()
                .map(Loader::get)
                .collect(Collectors.toList()));
    }

    @Override
    public @Nullable eu.software4you.ulib.impl.spigot.mappings.MappedField fieldFromSource(String originalName) {
        return Optional.ofNullable(fieldsBySourceName.get(originalName)).map(Loader::get).orElse(null);
    }

    @Override
    public @Nullable eu.software4you.ulib.impl.spigot.mappings.MappedField fieldFromMapped(String mappedName) {
        return Optional.ofNullable(fieldsByMappedName.get(mappedName)).map(Loader::get).orElse(null);
    }

    @Override
    public @NotNull Collection<MappedMethod> methods() {
        return Collections.unmodifiableCollection(methodsBySourceName.values().stream()
                .map(Loader::get)
                .collect(Collectors.toList()));
    }

    @Override
    public @Nullable eu.software4you.ulib.impl.spigot.mappings.MappedMethod methodFromSource(String originalName) {
        return Optional.ofNullable(methodsBySourceName.get(originalName)).map(Loader::get).orElse(null);
    }

    @Override
    public @Nullable eu.software4you.ulib.impl.spigot.mappings.MappedMethod methodFromMapped(String mappedName) {
        return Optional.ofNullable(methodsByMappedName.get(mappedName)).map(Loader::get).orElse(null);
    }
}
