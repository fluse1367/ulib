package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.ulib.Loader;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

final class ClassMapping extends ObfClass implements eu.software4you.spigot.mappings.ClassMapping {
    // \d+:\d+:(\S+) (\S+)\((?:(\S+),|(\S+))*\)
    // /\d+:\d+:(\S+) (\S+)\((?:(\S+),|(\S+))*\)/gmi
    private static final Pattern METHOD_MAPPING_PATTERN = Pattern.compile("\\d+:\\d+:(\\S+) (\\S+)\\((?:(\\S+),|(\\S+))*\\)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    // field pattern may also match methods, because field pattern is only tested for if method pattern does not match
    // (\S+) (\S+)
    // /(\S+) (\S+)/gmi
    private static final Pattern FIELD_MAPPING_PATTERN = Pattern.compile("(\\S+) (\\S+)",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private final Map<String, Loader<ObfField>> fieldsByOriginalName;
    private final Map<String, Loader<ObfField>> fieldsByObfuscatedName;
    private final Map<String, Loader<ObfMethod>> methodsByOriginalName;
    private final Map<String, Loader<ObfMethod>> methodsByObfuscatedName;

    ClassMapping(MapRoot root, String name, String obfuscatedName, Map<String, String> members) {
        super(name, obfuscatedName);

        Map<String, Loader<ObfField>> fieldsByOriginalName = new HashMap<>();
        Map<String, Loader<ObfField>> fieldsByObfuscatedName = new HashMap<>();
        Map<String, Loader<ObfMethod>> methodsByOriginalName = new HashMap<>();
        Map<String, Loader<ObfMethod>> methodsByObfuscatedName = new HashMap<>();
        members.forEach((original, obfuscated) -> {
            Matcher matcher;

            // TODO
            if ((matcher = METHOD_MAPPING_PATTERN.matcher(original)).matches()) {
                // is method
                String returnType = matcher.group(1);
                String methodName = matcher.group(2);
                String[] parameterTypes = new String[matcher.groupCount() - 2];
                for (int i = 3; i <= matcher.groupCount(); i++) {
                    parameterTypes[i] = matcher.group(i);
                }

                val loader = new Loader<>(() -> {
                    ObfClass[] params = new ObfClass[parameterTypes.length];
                    for (int i = 0; i < parameterTypes.length; i++) {
                        params[i] = root.getOrCreate(parameterTypes[i]);
                    }

                    return new ObfMethod(this, root.getOrCreate(returnType),
                            params, methodName, obfuscatedName);
                });

                methodsByOriginalName.put(methodName, loader);
                methodsByObfuscatedName.put(obfuscated, loader);
            } else if ((matcher = FIELD_MAPPING_PATTERN.matcher(original)).matches()) {
                // is field
                String type = matcher.group(1);
                String fieldName = matcher.group(2);

                val loader = new Loader<>(() -> new ObfField(this, root.getOrCreate(type),
                        fieldName, obfuscatedName));
                fieldsByOriginalName.put(fieldName, loader);
                fieldsByObfuscatedName.put(obfuscated, loader);
            } else {
                throw new IllegalStateException(String.format("Neither field nor method (%s)", original));
            }

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
