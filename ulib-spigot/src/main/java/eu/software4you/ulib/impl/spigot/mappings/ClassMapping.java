package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.ulib.core.api.common.collection.Triple;
import eu.software4you.spigot.mappings.MappedField;
import eu.software4you.spigot.mappings.MappedMethod;
import eu.software4you.ulib.Loader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class ClassMapping extends MappedClass implements eu.software4you.spigot.mappings.ClassMapping {
    protected final Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsBySourceName;
    protected final Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsByMappedName;
    protected final Map<String, Loader<List<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> methodsBySourceName;
    protected final Map<String, Loader<List<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> methodsByMappedName;

    ClassMapping(String sourceName, String mappedName,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<MappedClass, Supplier<eu.software4you.ulib.impl.spigot.mappings.MappedField>>>> fields,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<MappedClass, Supplier<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>>> methods) {
        super(sourceName, mappedName);

        Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsByOriginalName = new HashMap<>();
        Map<String, Loader<eu.software4you.ulib.impl.spigot.mappings.MappedField>> fieldsByMappedName = new HashMap<>();
        fields.forEach(t -> {
            var loader = new Loader<>(t.getThird().apply(this));
            fieldsByOriginalName.put(t.getFirst(), loader);
            fieldsByMappedName.put(t.getSecond(), loader);
        });

        Map<String, List<Supplier<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> _methodsByOriginalName = new HashMap<>();
        Map<String, List<Supplier<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> _methodsByMappedName = new HashMap<>();
        methods.forEach(t -> {
            if (!_methodsByOriginalName.containsKey(t.getFirst())) {
                _methodsByOriginalName.put(t.getFirst(), new ArrayList<>());
            }
            _methodsByOriginalName.get(t.getFirst()).add(t.getThird().apply(this));
            if (!_methodsByMappedName.containsKey(t.getSecond())) {
                _methodsByMappedName.put(t.getSecond(), new ArrayList<>());
            }
            _methodsByMappedName.get(t.getSecond()).add(t.getThird().apply(this));
        });

        Map<String, Loader<List<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> methodsByOriginalName = new HashMap<>();
        _methodsByOriginalName.forEach((name, list) -> methodsByOriginalName.put(name, new Loader<>(() -> list.stream()
                .map(Supplier::get)
                .collect(Collectors.toList()))));
        Map<String, Loader<List<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> methodsByMappedName = new HashMap<>();
        _methodsByMappedName.forEach((name, list) -> methodsByMappedName.put(name, new Loader<>(() -> list.stream()
                .map(Supplier::get)
                .collect(Collectors.toList()))));

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
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }

    @Override
    public @NotNull Collection<MappedMethod> methodsFromSource(String sourceName) {
        return Collections.unmodifiableCollection(Optional.ofNullable(methodsBySourceName.get(sourceName))
                .map(Loader::get)
                .orElse(Collections.emptyList()));
    }

    @Override
    public @Nullable eu.software4you.ulib.impl.spigot.mappings.MappedMethod methodFromSource(String sourceName, eu.software4you.spigot.mappings.MappedClass[] params) {
        return method(methodsBySourceName, sourceName, params);
    }

    @Override
    public @NotNull Collection<MappedMethod> methodsFromMapped(String mappedName) {
        return Collections.unmodifiableCollection(Optional.ofNullable(methodsByMappedName.get(mappedName))
                .map(Loader::get)
                .orElse(Collections.emptyList()));
    }

    @Override
    public @Nullable eu.software4you.ulib.impl.spigot.mappings.MappedMethod methodFromMapped(String mappedName, eu.software4you.spigot.mappings.MappedClass[] params) {
        return method(methodsByMappedName, mappedName, params);
    }

    private eu.software4you.ulib.impl.spigot.mappings.MappedMethod method(Map<String, Loader<List<eu.software4you.ulib.impl.spigot.mappings.MappedMethod>>> map,
                                                                          String name, eu.software4you.spigot.mappings.MappedClass[] params) {
        return Optional.ofNullable(map.get(name))
                .map(Loader::get)
                .flatMap(li -> li.stream()
                        .filter(mm -> Arrays.equals(mm.parameterTypes(), params))
                        .findFirst())
                .orElse(null);
    }
}
