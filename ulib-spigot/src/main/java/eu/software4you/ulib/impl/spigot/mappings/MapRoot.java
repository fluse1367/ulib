package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.spigot.mappings.JarMapping;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class MapRoot implements JarMapping {
    // \n(\S+) -> (\S+):(?:\n    (.+) -> (\S+))*
    // /\n(\S+) -> (\S+):(?:\n    (.+) -> (\S+))*/gmi
    private static final Pattern CLASS_MAPPING_PATTERN = Pattern.compile("\\n(\\S+) -> (\\S+):(?:\\n    (.+) -> (\\S+))*",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private final Map<String, ClassMapping> byOriginalName;
    private final Map<String, ClassMapping> byObfuscatedName;
    private final Map<String, ObfClass> createdBuffer = new ConcurrentHashMap<>();

    MapRoot(String mappingData) {
        Map<String, ClassMapping> byOriginalName = new HashMap<>();
        Map<String, ClassMapping> byObfuscatedName = new HashMap<>();

        Matcher matcher = CLASS_MAPPING_PATTERN.matcher(mappingData);
        while (matcher.find()) { // iterate over every class mapping
            // next class ALWAYS has group 1 & 2
            String originalName = matcher.group(1);
            String obfuscatedName = matcher.group(2);

            Map<String, String> members = new HashMap<>();
            for (int i = 3; i <= matcher.groupCount(); i += 2) {
                // always has 2 more groups
                String original = matcher.group(i);
                String obfuscated = matcher.group(i + 1);

                members.put(original, obfuscated);
            }

            val mapping = new ClassMapping(this, originalName, obfuscatedName, members);
            byOriginalName.put(originalName, mapping);
            byObfuscatedName.put(obfuscatedName, mapping);
        }

        this.byOriginalName = Collections.unmodifiableMap(byOriginalName);
        this.byObfuscatedName = Collections.unmodifiableMap(byObfuscatedName);
    }

    @Override
    public @NotNull Collection<eu.software4you.spigot.mappings.ClassMapping> getAll() {
        return Collections.unmodifiableCollection(byOriginalName.values());
    }

    @Override
    public @Nullable ClassMapping get(@NotNull String originalName) {
        return byOriginalName.get(originalName);
    }

    @NotNull
    ObfClass getOrCreate(String originalName) {
        if (byOriginalName.containsKey(originalName))
            return byOriginalName.get(originalName);
        if (!createdBuffer.containsKey(originalName))
            createdBuffer.put(originalName, new ClassMapping(this, originalName, originalName, Collections.emptyMap()));
        return createdBuffer.get(originalName);
    }

    @Override
    public @Nullable ClassMapping search(@NotNull String obfuscatedName) {
        return byObfuscatedName.get(obfuscatedName);
    }
}
