package eu.software4you.ulib.spigot.impl.mappings;

import eu.software4you.ulib.core.api.common.collection.Triple;
import eu.software4you.ulib.core.api.utils.LazyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

final class ClassMapping extends MappedClass implements eu.software4you.ulib.spigot.api.mappings.ClassMapping {
    protected final Map<String, LazyValue<MappedField>> fieldsBySourceName;
    protected final Map<String, LazyValue<MappedField>> fieldsByMappedName;
    protected final Map<String, LazyValue<List<MappedMethod>>> methodsBySourceName;
    protected final Map<String, LazyValue<List<MappedMethod>>> methodsByMappedName;

    ClassMapping(String sourceName, String mappedName,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> fields,
                 // triple: name, obfName, loadTaskGenerator
                 Collection<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> methods) {
        super(sourceName, mappedName);

        Map<String, LazyValue<MappedField>> fieldsByOriginalName = new HashMap<>();
        Map<String, LazyValue<MappedField>> fieldsByMappedName = new HashMap<>();
        fields.forEach(t -> {
            var loader = new LazyValue<>(t.getThird().apply(this));
            fieldsByOriginalName.put(t.getFirst(), loader);
            fieldsByMappedName.put(t.getSecond(), loader);
        });

        Map<String, List<Supplier<MappedMethod>>> _methodsByOriginalName = new HashMap<>();
        Map<String, List<Supplier<MappedMethod>>> _methodsByMappedName = new HashMap<>();
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

        Map<String, LazyValue<List<MappedMethod>>> methodsByOriginalName = new HashMap<>();
        _methodsByOriginalName.forEach((name, list) -> methodsByOriginalName.put(name, new LazyValue<>(() -> list.stream()
                .map(Supplier::get)
                .collect(Collectors.toList()))));
        Map<String, LazyValue<List<MappedMethod>>> methodsByMappedName = new HashMap<>();
        _methodsByMappedName.forEach((name, list) -> methodsByMappedName.put(name, new LazyValue<>(() -> list.stream()
                .map(Supplier::get)
                .collect(Collectors.toList()))));

        this.fieldsBySourceName = Collections.unmodifiableMap(fieldsByOriginalName);
        this.fieldsByMappedName = Collections.unmodifiableMap(fieldsByMappedName);
        this.methodsBySourceName = Collections.unmodifiableMap(methodsByOriginalName);
        this.methodsByMappedName = Collections.unmodifiableMap(methodsByMappedName);
    }

    @Override
    public @NotNull Collection<eu.software4you.ulib.spigot.api.mappings.MappedField> fields() {
        return Collections.unmodifiableCollection(fieldsBySourceName.values().stream()
                .map(LazyValue::get)
                .collect(Collectors.toList()));
    }

    @Override
    public @Nullable MappedField fieldFromSource(String originalName) {
        return Optional.ofNullable(fieldsBySourceName.get(originalName)).map(LazyValue::get).orElse(null);
    }

    @Override
    public @Nullable MappedField fieldFromMapped(String mappedName) {
        return Optional.ofNullable(fieldsByMappedName.get(mappedName)).map(LazyValue::get).orElse(null);
    }

    @Override
    public @NotNull Collection<eu.software4you.ulib.spigot.api.mappings.MappedMethod> methods() {
        return Collections.unmodifiableCollection(methodsBySourceName.values().stream()
                .map(LazyValue::get)
                .flatMap(Collection::stream)
                .collect(Collectors.toList()));
    }

    @Override
    public @NotNull Collection<eu.software4you.ulib.spigot.api.mappings.MappedMethod> methodsFromSource(String sourceName) {
        return Collections.unmodifiableCollection(Optional.ofNullable(methodsBySourceName.get(sourceName))
                .map(LazyValue::get)
                .orElse(Collections.emptyList()));
    }

    @Override
    public @Nullable MappedMethod methodFromSource(String sourceName, eu.software4you.ulib.spigot.api.mappings.MappedClass[] params) {
        return method(methodsBySourceName, sourceName, params);
    }

    @Override
    public @NotNull Collection<eu.software4you.ulib.spigot.api.mappings.MappedMethod> methodsFromMapped(String mappedName) {
        return Collections.unmodifiableCollection(Optional.ofNullable(methodsByMappedName.get(mappedName))
                .map(LazyValue::get)
                .orElse(Collections.emptyList()));
    }

    @Override
    public @Nullable MappedMethod methodFromMapped(String mappedName, eu.software4you.ulib.spigot.api.mappings.MappedClass[] params) {
        return method(methodsByMappedName, mappedName, params);
    }

    private MappedMethod method(Map<String, LazyValue<List<MappedMethod>>> map,
                                String name, eu.software4you.ulib.spigot.api.mappings.MappedClass[] params) {
        return Optional.ofNullable(map.get(name))
                .map(LazyValue::get)
                .flatMap(li -> li.stream()
                        .filter(mm -> Arrays.equals(mm.parameterTypes(), params))
                        .findFirst())
                .orElse(null);
    }
}
