package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.common.collection.Pair;
import eu.software4you.common.collection.Triple;
import lombok.val;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class BukkitMapping extends MappingRoot<Pair<String, String>> implements eu.software4you.spigot.mappings.BukkitMapping {
    // /^(\S++) (\S++)$/gmi
    private static final Pattern CLASS_MAPPING_PATTERN = Pattern.compile("^(\\S++) (\\S++)$",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    // /^\Q...\E (\S++) (\S++)$/gm
    private static final Function<String, Pattern> FIELD_MAPPING_PATTERN = clazz ->
            Pattern.compile(String.format("^\\Q%s\\E (\\S++) (\\S++)$", clazz));

    // /^\Q...\E (\S++) \((\S*)\)(\S++) (\S++)$/gm
    private static final Function<String, Pattern> METHOD_MAPPING_PATTERN = clazz ->
            Pattern.compile(String.format("^\\Q%s\\E (\\S++) \\((\\S*)\\)(\\S++) (\\S++)$", clazz));

    BukkitMapping(String classData, String memberData) {
        super(new Pair<>(classData, memberData));
    }

    private static List<String> decodeSignatures(String sigs) {
        List<String> types = new ArrayList<>();

        int pos = 0;
        // https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html

        while (pos < sigs.length()) {
            switch (sigs.charAt(pos)) {
                case 'Z':
                    types.add("boolean");
                    break;
                case 'B':
                    types.add("byte");
                    break;
                case 'C':
                    types.add("char");
                    break;
                case 'S':
                    types.add("short");
                    break;
                case 'I':
                    types.add("int");
                    break;
                case 'J':
                    types.add("long");
                    break;
                case 'F':
                    types.add("float");
                    break;
                case 'D':
                    types.add("double");
                    break;
                case 'L':
                    int pos2 = sigs.indexOf(";", pos);
                    String type = sigs.substring(pos + 1, pos2).replace('/', '.');
                    types.add(type);
                    pos = pos2;
                    break;
                case '[':
                    String rest = sigs.substring(pos + 1);
                    List<String> restTypes = decodeSignatures(rest);
                    // first of rest types is an array
                    restTypes.set(0, restTypes.get(0) + "[]");
                    types.addAll(restTypes);
                    return types;
            }
            pos++;
        }

        return types;
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(Pair<String, String> mappingData) {
        Map<String, ClassMapping> byVanillaName = new HashMap<>();
        Map<String, ClassMapping> byBukkitName = new HashMap<>();

        String classMapping = mappingData.getFirst();
        String memberMapping = mappingData.getSecond();

        Matcher classMatcher = CLASS_MAPPING_PATTERN.matcher(classMapping);
        while (classMatcher.find()) {
            String vanillaName = classMatcher.group(1);
            String bukkitName = classMatcher.group(2);

            val fieldMatcher = FIELD_MAPPING_PATTERN.apply(bukkitName).matcher(memberMapping);
            val fields = mapFields(fieldMatcher);

            val methodMatcher = METHOD_MAPPING_PATTERN.apply(bukkitName).matcher(memberMapping);
            val methods = mapMethods(methodMatcher);

            vanillaName = vanillaName.replace('/', '.');
            bukkitName = bukkitName.replace('/', '.');

            ClassMapping mapping = new ClassMapping(vanillaName, bukkitName, fields, methods);
            byVanillaName.put(vanillaName, mapping);
            byBukkitName.put(bukkitName, mapping);
        }

        return new Pair<>(byVanillaName, byBukkitName);
    }

    private List<Triple<String, String, Function<ObfClass, Supplier<ObfField>>>> mapFields(Matcher matcher) {
        // triple: vanillaName, bukkitName, loader
        List<Triple<String, String, Function<ObfClass, Supplier<ObfField>>>> fields = new ArrayList<>();
        while (matcher.find()) {
            String vanillaName = matcher.group(1);
            String bukkitName = matcher.group(2);

            Function<ObfClass, Supplier<ObfField>> loadTaskGenerator = parent -> () ->
                    // type = null bc mapping does not contain field types
                    new ObfField(parent, null, vanillaName, bukkitName);
            fields.add(new Triple<>(vanillaName, bukkitName, loadTaskGenerator));
        }

        return fields;
    }

    private List<Triple<String, String, Function<ObfClass, Supplier<ObfMethod>>>> mapMethods(Matcher matcher) {
        // triple: vanillaName, bukkitName, loader
        List<Triple<String, String, Function<ObfClass, Supplier<ObfMethod>>>> methods = new ArrayList<>();
        while (matcher.find()) {
            String vanillaName = matcher.group(1);
            String bukkitName = matcher.group(4);

            String group2 = matcher.group(2);
            String[] parameterTypes = group2 != null ? decodeSignatures(group2).toArray(new String[0]) : new String[0];
            String returnType = decodeSignatures(matcher.group(3)).get(0);


            Function<ObfClass, Supplier<ObfMethod>> loadTaskGenerator = parent -> () -> {
                ObfClass[] paramTypes = new ObfClass[parameterTypes.length];
                for (int i = 0; i < paramTypes.length; i++) {
                    paramTypes[i] = getOrCreateDummy(parameterTypes[i]);
                }

                return new ObfMethod(parent, getOrCreateDummy(returnType),
                        paramTypes, vanillaName, bukkitName);
            };
            methods.add(new Triple<>(vanillaName, bukkitName, loadTaskGenerator));
        }
        return methods;
    }
}
