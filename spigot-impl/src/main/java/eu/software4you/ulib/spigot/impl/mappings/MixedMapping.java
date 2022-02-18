package eu.software4you.ulib.spigot.impl.mappings;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.common.collection.Triple;
import eu.software4you.ulib.core.api.util.LazyValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

import static eu.software4you.ulib.core.ULib.logger;

final class MixedMapping extends MappingRoot<Pair<BukkitMapping, VanillaMapping>> implements eu.software4you.ulib.spigot.api.mappings.MixedMapping {
    MixedMapping(BukkitMapping bm, VanillaMapping vm) {
        super(new Pair<>(bm, vm));
    }

    @Override
    public @Nullable ClassMapping from(@NotNull Class<?> source) {
        return fromSource(source.getName());
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(Pair<BukkitMapping, VanillaMapping> mappingData) {
        logger().finer("Generating mixed mappings");

        var bm = mappingData.getFirst();
        var vm = mappingData.getSecond();

        Map<String, ClassMapping> byVanillaSource = new HashMap<>();
        Map<String, ClassMapping> byBukkit = new HashMap<>();

        bm.byMappedName.forEach((name, cm) -> {
            String bukkitName = cm.mappedName();

            String vanillaObfName = cm.sourceName();
            var vc = vm.byMappedName.get(vanillaObfName);

            String vanillaSourceName = vc.sourceName();

            logger().finest(() -> String.format("Class Mapping: %s -> %s", vanillaSourceName, bukkitName));


            ClassMapping switched = new ClassMapping(vanillaSourceName, bukkitName,
                    mapFields(vc.fieldsBySourceName.values(), cm),
                    mapMethods(vc.methodsBySourceName.values(), cm));

            byVanillaSource.put(vanillaSourceName, switched);
            byBukkit.put(bukkitName, switched);
        });

        logger().finer("Mixed mappings generation finished");
        return new Pair<>(byVanillaSource, byBukkit);
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> mapFields(
            Collection<LazyValue<MappedField>> vanillaFields, ClassMapping bukkitResolve) {
        // triple: vanillaSourceName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> fields = new ArrayList<>();

        vanillaFields.forEach(loader -> {
            var vf = loader.get();

            String vanillaSourceName = vf.sourceName();
            String vanillaObfName = vf.mappedName();
            String bukkitName = Optional.ofNullable(bukkitResolve.fieldsBySourceName.get(vanillaObfName))
                    .map(LazyValue::get).map(Mapped::mappedName)
                    .orElseGet(() -> {
                        // fall back to vanilla obf name
                        logger().finest(() -> String.format("field %s (originally %s) not found in bukkit (vanilla obf -> bukkit) mappings",
                                vanillaObfName, vanillaSourceName));
                        return vanillaObfName;
                    });

            logger().finest(() -> String.format("Class Member (field): %s -> %s", vanillaSourceName, bukkitName));

            Function<MappedClass, Supplier<MappedField>> loadTaskGenerator = parent -> () -> new MappedField(
                    parent, vf.type(), vanillaSourceName, bukkitName
            );
            fields.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
        });

        return fields;
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> mapMethods(
            Collection<LazyValue<List<MappedMethod>>> vanillaMethods, ClassMapping bukkitResolve) {
        // triple: vanillaSourceName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> methods = new ArrayList<>();

        vanillaMethods.forEach(loader -> {
            var li = loader.get();

            li.forEach(vm -> {
                String vanillaSourceName = vm.sourceName();
                String vanillaObfName = vm.mappedName();
                String bukkitName = Optional.ofNullable(bukkitResolve.methodFromSource(vanillaObfName, vm.parameterTypes()))
                        .map(Mapped::mappedName)
                        .orElseGet(() -> {
                            // fall back to vanilla obf name
                            logger().finest(() -> String.format("method %s (originally %s) not found in bukkit (vanilla obf -> bukkit) mappings",
                                    vanillaObfName, vanillaSourceName));
                            return vanillaObfName;
                        });

                logger().finest(() -> String.format("Member (method): %s -> %s", vanillaSourceName, bukkitName));

                Function<MappedClass, Supplier<MappedMethod>> loadTaskGenerator = parent -> () -> new MappedMethod(parent,
                        vm.returnType(), vm.parameterTypes(), vanillaSourceName, bukkitName);
                methods.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
            });

        });

        return methods;
    }
}
