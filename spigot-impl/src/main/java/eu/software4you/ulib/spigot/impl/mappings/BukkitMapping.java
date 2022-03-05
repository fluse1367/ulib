package eu.software4you.ulib.spigot.impl.mappings;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.common.collection.Triple;
import eu.software4you.ulib.spigot.api.multiversion.Protocol;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static eu.software4you.ulib.core.ULib.logger;

final class BukkitMapping extends MappingRoot<Triple<String, String, Protocol>> implements eu.software4you.ulib.spigot.api.mappings.BukkitMapping {
    // /^(\S++) (\S++)$/gmi
    private static final Pattern CLASS_MAPPING_PATTERN = Pattern.compile("^(\\S++) (\\S++)$",
            Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    // /^\Q...\E (\S++) (\S++)$/gmi
    private static final Function<String, Pattern> FIELD_MAPPING_PATTERN = clazz ->
            Pattern.compile(String.format("^\\Q%s\\E (\\S++) (\\S++)$", clazz),
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    // /^\Q...\E (\S++) \((\S*)\)(\S++) (\S++)$/gmi
    private static final Function<String, Pattern> METHOD_MAPPING_PATTERN = clazz ->
            Pattern.compile(String.format("^\\Q%s\\E (\\S++) \\((\\S*)\\)(\\S++) (\\S++)$", clazz),
                    Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);

    private Protocol version;

    BukkitMapping(String classData, String memberData, Protocol version) {
        super(new Triple<>(classData, memberData, version));
    }

    private static List<String> decodeSignatures(String sigs) {
        List<String> types = new ArrayList<>();

        int pos = 0;
        // https://docs.oracle.com/javase/8/docs/technotes/guides/jni/spec/types.html

        while (pos < sigs.length()) {
            switch (sigs.charAt(pos)) {
                case 'Z' -> types.add("boolean");
                case 'B' -> types.add("byte");
                case 'C' -> types.add("char");
                case 'S' -> types.add("short");
                case 'I' -> types.add("int");
                case 'J' -> types.add("long");
                case 'F' -> types.add("float");
                case 'D' -> types.add("double");
                case 'V' -> types.add("void");
                case 'L' -> {
                    int pos2 = sigs.indexOf(";", pos);
                    String type = sigs.substring(pos + 1, pos2).replace('/', '.');
                    types.add(type);
                    pos = pos2;
                }
                case '[' -> {
                    String rest = sigs.substring(pos + 1);
                    List<String> restTypes = decodeSignatures(rest);
                    // first of rest types is an array
                    restTypes.set(0, restTypes.get(0) + "[]");
                    types.addAll(restTypes);
                    return types;
                }
            }
            pos++;
        }

        return types;
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(Triple<String, String, Protocol> mappingData) {
        logger().finer("Generating Bukkit mappings");
        this.version = mappingData.getThird();

        Map<String, ClassMapping> byVanillaName = new HashMap<>();
        Map<String, ClassMapping> byBukkitName = new HashMap<>();

        String classMapping = mappingData.getFirst();
        String memberMapping = mappingData.getSecond();

        Matcher classMatcher = CLASS_MAPPING_PATTERN.matcher(classMapping);
        while (classMatcher.find()) {
            logger().finest(() -> "Class Mapping match: " + classMatcher.group());

            String vanillaName = classMatcher.group(1).replace('/', '.');
            String bukkitNameRaw = classMatcher.group(2);
            String bukkitName = nms(bukkitNameRaw).replace('/', '.');

            logger().finest(() -> String.format("Class Mapping: %s -> %s (raw: %s)", vanillaName, bukkitName, bukkitNameRaw));

            var fields = mapFields(memberMapping == null ? null
                    : FIELD_MAPPING_PATTERN.apply(bukkitNameRaw).matcher(memberMapping));

            var methods = mapMethods(memberMapping == null ? null
                    : METHOD_MAPPING_PATTERN.apply(bukkitNameRaw).matcher(memberMapping));


            ClassMapping mapping = new ClassMapping(vanillaName, bukkitName, fields, methods);
            byVanillaName.put(vanillaName, mapping);
            byBukkitName.put(bukkitName, mapping);
        }

        logger().finer("Bukkit mappings generation finished");
        return new Pair<>(byVanillaName, byBukkitName);
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> mapFields(Matcher matcher) {
        if (matcher == null)
            return Collections.emptyList();

        // triple: vanillaName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> fields = new ArrayList<>();
        while (matcher.find()) {
            logger().finest(() -> String.format("Member match (field): %s", matcher.group()));

            String vanillaName = matcher.group(1);
            String bukkitName = matcher.group(2);

            logger().finest(() -> String.format("Class member (field): %s -> %s", vanillaName, bukkitName));

            Function<MappedClass, Supplier<MappedField>> loadTaskGenerator = parent -> () ->
                    // type = null bc mapping does not contain field types
                    new MappedField(parent, null, vanillaName, bukkitName);
            fields.add(new Triple<>(vanillaName, bukkitName, loadTaskGenerator));
        }

        return fields;
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> mapMethods(Matcher matcher) {
        if (matcher == null)
            return Collections.emptyList();

        // triple: vanillaName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> methods = new ArrayList<>();
        while (matcher.find()) {
            logger().finest(() -> String.format("Member match (method): %s", matcher.group()));

            String vanillaName = matcher.group(1);
            String bukkitName = matcher.group(4);

            String group2 = matcher.group(2);
            String[] parameterTypes = group2 != null ? decodeSignatures(group2).toArray(new String[0]) : new String[0];
            String returnType = decodeSignatures(matcher.group(3)).get(0);

            methods.add(method(returnType, parameterTypes, vanillaName, bukkitName));
        }
        return methods;
    }

    private String nms(String cl) {
        if (version.atLeast(Protocol.v1_17_R1))
            return cl;
        String clazz = cl.substring(cl.lastIndexOf("/") + 1);
        return "net/minecraft/server/" + version.name() + "/" + clazz;
    }
}
