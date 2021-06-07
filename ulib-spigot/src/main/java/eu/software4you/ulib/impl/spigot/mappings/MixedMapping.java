package eu.software4you.ulib.impl.spigot.mappings;

import eu.software4you.common.collection.Pair;
import eu.software4you.common.collection.Triple;
import eu.software4you.ulib.Loader;
import eu.software4you.ulib.ULib;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

final class MixedMapping extends MappingRoot<Pair<BukkitMapping, VanillaMapping>> implements eu.software4you.spigot.mappings.MixedMapping {
    MixedMapping(BukkitMapping bm, VanillaMapping vm) {
        super(new Pair<>(bm, vm));
    }

    @Override
    public @Nullable ClassMapping from(@NotNull Class<?> source) {
        return fromSource(source.getName());
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(Pair<BukkitMapping, VanillaMapping> mappingData) {
        val bm = mappingData.getFirst();
        val vm = mappingData.getSecond();

        Map<String, ClassMapping> byBukkit = new HashMap<>();
        Map<String, ClassMapping> byVanillaSource = new HashMap<>();

        bm.byMappedName.forEach((name, cm) -> {
            String bukkitName = cm.mappedName();

            String vanillaObfName = cm.sourceName();
            val vc = vm.byMappedName.get(vanillaObfName);

            String vanillaSourceName = vc.sourceName();


            ClassMapping switched = new ClassMapping(bukkitName, vanillaSourceName,
                    mapFields(vc.fieldsBySourceName.values(), cm),
                    mapMethods(vc.methodsBySourceName.values(), cm));

            byBukkit.put(bukkitName, switched);
            byVanillaSource.put(vanillaSourceName, switched);
        });

        return new Pair<>(byBukkit, byVanillaSource);
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> mapFields(
            Collection<Loader<MappedField>> vanillaFields, ClassMapping bukkitResolve) {
        // triple: vanillaSourceName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedField>>>> fields = new ArrayList<>();

        vanillaFields.forEach(loader -> {
            val vf = loader.get();

            String vanillaSourceName = vf.mappedName();
            String vanillaObfName = vf.sourceName();
            String bukkitName = Optional.ofNullable(bukkitResolve.fieldsBySourceName.get(vanillaObfName))
                    .map(Loader::get).map(Mapped::mappedName)
                    .orElseGet(() -> {
                        // fall back to vanilla obf name
                        ULib.logger().finest(() -> String.format("field %s not found in bukkit (vanilla obf -> bukkit) mappings", vanillaObfName));
                        return vanillaObfName;
                    });

            Function<MappedClass, Supplier<MappedField>> loadTaskGenerator = parent -> () -> new MappedField(
                    parent, vf.type(), vanillaSourceName, bukkitName
            );
            fields.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
        });

        return fields;
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> mapMethods(
            Collection<Loader<MappedMethod>> vanillaMethods, ClassMapping bukkitResolve) {
        // triple: vanillaSourceName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> methods = new ArrayList<>();

        vanillaMethods.forEach(loader -> {
            val vm = loader.get();

            String vanillaSourceName = vm.sourceName();
            String vanillaObfName = vm.mappedName();
            String bukkitName = Optional.ofNullable(bukkitResolve.methodsBySourceName.get(vanillaObfName))
                    .map(Loader::get).map(Mapped::mappedName)
                    .orElseGet(() -> {
                        // fall back to vanilla obf name
                        ULib.logger().finest(() -> String.format("method %s not found in bukkit (vanilla obf -> bukkit) mappings", vanillaObfName));
                        return vanillaObfName;
                    });

            Function<MappedClass, Supplier<MappedMethod>> loadTaskGenerator = parent -> () -> new MappedMethod(parent,
                    vm.returnType(), vm.parameterTypes(), vanillaSourceName, bukkitName);
            methods.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
        });

        return methods;
    }
}
