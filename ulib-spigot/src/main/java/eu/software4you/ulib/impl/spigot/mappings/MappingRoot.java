package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.common.collection.Pair;
import eu.software4you.spigot.mappings.JarMapping;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class MappingRoot<T> implements JarMapping {

    protected final Map<String, ClassMapping> bySourceName;
    protected final Map<String, ClassMapping> byMappedName;
    private final Map<String, MappedClass> dummies = new ConcurrentHashMap<>();

    MappingRoot(final T mappingData) {
        val mappings = generateMappings(mappingData);
        this.bySourceName = Collections.unmodifiableMap(mappings.getFirst());
        this.byMappedName = Collections.unmodifiableMap(mappings.getSecond());
    }

    /**
     * @return pair first: mappings by original name; pair second: mappings by mapped name
     */
    protected abstract Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(T mappingData);

    @Override
    public @NotNull Collection<eu.software4you.spigot.mappings.ClassMapping> all() {
        return Collections.unmodifiableCollection(bySourceName.values());
    }

    @Override
    public @Nullable ClassMapping fromSource(@NotNull String originalName) {
        return bySourceName.get(originalName);
    }

    @Override
    public @Nullable ClassMapping fromMapped(@NotNull String mappedName) {
        return byMappedName.get(mappedName);
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
}
