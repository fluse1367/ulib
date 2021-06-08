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

import static eu.software4you.ulib.ULib.logger;
import static java.util.regex.Pattern.CASE_INSENSITIVE;
import static java.util.regex.Pattern.MULTILINE;

final class VanillaMapping extends MappingRoot<String> implements eu.software4you.spigot.mappings.VanillaMapping {
    /*
     pattern to find a whole class mapping (including members)
     original regex:                    /^(\S+) -> (\S+):$\n?((?:^ {4}\S+ \S+ -> \S+$\n?)*)/gmi
     regex with possessive quantifiers: /^(\S++) -> (\S+):$\n?+((?:^ {4}+\S++ \S++ -> \S++$\n?+)*+)/gmi
     Using possessive quantifiers reduces the changes of a StackOverflowError: This is necessary because the mapping string is quite long.
     */
    private static final Pattern CLASS_MAPPING_PATTERN = Pattern.compile("^(\\S++) -> (\\S+):$\\n?+((?:^ {4}+\\S++ \\S++ -> \\S++$\\n?+)*+)",
            MULTILINE | CASE_INSENSITIVE);

    /*
     pattern to find class member mappings (fields only)
     regex: /^ {4}([^\r\n\t\f\v :]+) ([^<>()\r\n\t\f\v ]+) -> (\S+)$/gmi
     */
    private static final Pattern CLASS_MEMBER_FIELDS_PATTERN = Pattern.compile("^ {4}([^\\r\\n\\t\\f\\v :]+) ([^<>()\\r\\n\\t\\f\\v ]+) -> (\\S+)$",
            MULTILINE | CASE_INSENSITIVE);

    /*
     pattern to find class member mappings (methods only, except <init> and <clinit>)
     regex: /^ {4}(?:\d+:\d+:)?(\S+) ([^<>\r\n\t\f\v ]+)\((\S+)*\) -> (\S+)$/gmi
     */
    private static final Pattern CLASS_MEMBER_METHODS_PATTERN = Pattern.compile("^ {4}(?:\\d+:\\d+:)?(\\S+) ([^<>\\r\\n\\t\\f\\v ]+)\\((\\S+)*\\) -> (\\S+)$",
            MULTILINE | CASE_INSENSITIVE);

    VanillaMapping(String mappingData) {
        super(mappingData);
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(String mappingData) {
        /*
            TODO: If an StackOverflowError occurs *although* possessive quantifiers are used in the regex:
                  Split mapping data into chunks and handle them separately.
         */
        return mapClasses(mappingData, CLASS_MAPPING_PATTERN);
    }

    private Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> mapClasses(String mappingData, Pattern pattern) {
        logger().finer("Generating Vanilla mappings");

        Map<String, ClassMapping> byOriginalName = new HashMap<>();
        Map<String, ClassMapping> byObfuscatedName = new HashMap<>();

        // iterate over each mapped class
        Matcher classMatcher = pattern.matcher(mappingData);
        while (classMatcher.find()) { // iterate over every class mapping
            // next class ALWAYS has group 1-3
            logger().finest(() -> String.format("Class Mapping Match (%d): %s", classMatcher.groupCount(), classMatcher.group()));

            // group 1: original class name
            String originalName = classMatcher.group(1);
            // group 2: obfuscated class name
            String obfuscatedName = classMatcher.group(2);

            logger().finest(() -> String.format("Class Mapping: %s -> %s", originalName, obfuscatedName));

            // group 3: members
            String members = classMatcher.group(3);
            val fields = mapFields(members);
            val methods = mapMethods(members);


            ClassMapping mapping = new ClassMapping(originalName, obfuscatedName, fields, methods);
            byOriginalName.put(originalName, mapping);
            byObfuscatedName.put(obfuscatedName, mapping);
        }

        logger().finer("Vanilla mappings generation finished");
        return new Pair<>(byOriginalName, byObfuscatedName);
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> mapFields(String membersData) {
        // process fields
        Matcher fieldsMatcher = CLASS_MEMBER_FIELDS_PATTERN.matcher(membersData);
        // triple: name, obfName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> fields = new ArrayList<>();
        while (fieldsMatcher.find()) {
            logger().finest(() -> String.format("Member match (field): %s", fieldsMatcher.group()));

            String type = fieldsMatcher.group(1);
            String name = fieldsMatcher.group(2);
            String obfName = fieldsMatcher.group(3);

            logger().finest(() -> String.format("Member (field of type %s): %s -> %s", type, name, obfName));

            Function<MappedClass, Supplier<MappedField>> loadTaskGenerator = parent -> () -> new MappedField(parent,
                    getOrCreateDummy(type), name, obfName);
            fields.add(new Triple<>(name, obfName, loadTaskGenerator));
        }

        return fields;
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> mapMethods(String membersData) {
        // process methods
        Matcher methodsMatcher = CLASS_MEMBER_METHODS_PATTERN.matcher(membersData);
        // triple: name, obfName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> methods = new ArrayList<>();
        while (methodsMatcher.find()) {
            logger().finest(() -> String.format("Member match (method): %s", methodsMatcher.group()));

            String returnType = methodsMatcher.group(1);
            String name = methodsMatcher.group(2);
            String group3 = methodsMatcher.group(3);
            String[] parameterTypes = group3 != null ? group3.split(",") : new String[0];
            String obfName = methodsMatcher.group(4);

            methods.add(method(returnType, parameterTypes, name, obfName));
        }
        return methods;
    }


}
