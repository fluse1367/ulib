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

abstract class MappingRoot implements JarMapping {

    protected final Map<String, ClassMapping> byOriginalName;
    protected final Map<String, ClassMapping> byObfuscatedName;
    private final Map<String, ObfClass> dummies = new ConcurrentHashMap<>();

    MappingRoot(final String mappingData) {
        val mappings = generateMappings(mappingData);
        this.byOriginalName = Collections.unmodifiableMap(mappings.getFirst());
        this.byObfuscatedName = Collections.unmodifiableMap(mappings.getSecond());
    }

    /**
     * @return pair first: mappings by original name; pair second: mappings by obfuscated name
     */
    protected abstract Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(String mappingData);

    @Override
    public @NotNull Collection<eu.software4you.spigot.mappings.ClassMapping> getAll() {
        return Collections.unmodifiableCollection(byOriginalName.values());
    }

    @Override
    public @Nullable ClassMapping get(@NotNull String originalName) {
        return byOriginalName.get(originalName);
    }

    @Override
    public @Nullable ClassMapping search(@NotNull String obfuscatedName) {
        return byObfuscatedName.get(obfuscatedName);
    }

    @NotNull
    protected ObfClass getOrCreateDummy(String originalName) {
        if (byOriginalName.containsKey(originalName)) {
            return byOriginalName.get(originalName);
        }
        if (!dummies.containsKey(originalName)) {
            dummies.put(originalName, new ClassMapping(originalName, originalName,
                    Collections.emptyList(), Collections.emptyList()));
        }
        return dummies.get(originalName);
    }
}
