package eu.software4you.ulib.minecraft.impl.mappings;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.collection.Triple;
import eu.software4you.ulib.minecraft.mappings.JarMapping;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

abstract class MappingRoot<T> implements JarMapping {

    protected final Map<String, ClassMapping> bySourceName;
    protected final Map<String, ClassMapping> byMappedName;
    private final Map<String, MappedClass> dummies = new ConcurrentHashMap<>();

    MappingRoot(final T mappingData) {
        var mappings = generateMappings(mappingData);
        this.bySourceName = Collections.unmodifiableMap(mappings.getFirst());
        this.byMappedName = Collections.unmodifiableMap(mappings.getSecond());
    }

    /**
     * @return pair first: mappings by original name; pair second: mappings by mapped name
     */
    protected abstract Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(T mappingData);

    @Override
    public @NotNull Collection<eu.software4you.ulib.minecraft.mappings.ClassMapping> all() {
        return Collections.unmodifiableCollection(bySourceName.values());
    }

    @Override
    public @NotNull Optional<eu.software4you.ulib.minecraft.mappings.ClassMapping> fromSource(@NotNull String originalName) {
        return Optional.ofNullable(bySourceName.get(originalName));
    }

    @Override
    public @NotNull Optional<eu.software4you.ulib.minecraft.mappings.ClassMapping> fromMapped(@NotNull String mappedName) {
        return Optional.ofNullable(byMappedName.get(mappedName));
    }

    @NotNull
    protected MappedClass getOrCreateDummy(String originalName) {
        if (bySourceName.containsKey(originalName)) {
            return bySourceName.get(originalName);
        }
        if (!dummies.containsKey(originalName)) {
            dummies.put(originalName, new ClassMapping(originalName, originalName,
                    Collections.emptyList(), Collections.emptyList()));
        }
        return dummies.get(originalName);
    }

    protected Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>> method(String returnType, String[] parameterTypes, String sourceName, String mappedName) {

        Function<MappedClass, Supplier<MappedMethod>> loadTaskGenerator = parent -> () -> {
            MappedClass[] paramTypes = new MappedClass[parameterTypes.length];
            for (int i = 0; i < paramTypes.length; i++) {
                paramTypes[i] = getOrCreateDummy(parameterTypes[i]);
            }

            return new MappedMethod(parent, getOrCreateDummy(returnType),
                    paramTypes, sourceName, mappedName);
        };

        return new Triple<>(sourceName, mappedName, loadTaskGenerator);
    }
}
