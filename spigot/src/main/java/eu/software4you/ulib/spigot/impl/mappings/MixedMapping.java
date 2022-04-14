package eu.software4you.ulib.spigot.impl.mappings;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.collection.Triple;
import eu.software4you.ulib.core.util.LazyValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;


final class MixedMapping extends MappingRoot<Pair<BukkitMapping, VanillaMapping>> implements eu.software4you.ulib.spigot.mappings.MixedMapping {
    MixedMapping(BukkitMapping bm, VanillaMapping vm) {
        super(new Pair<>(bm, vm));
    }

    @Override
    public @NotNull Optional<eu.software4you.ulib.spigot.mappings.ClassMapping> from(@NotNull Class<?> source) {
        return fromSource(source.getName());
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(Pair<BukkitMapping, VanillaMapping> mappingData) {
        var bm = mappingData.getFirst();
        var vm = mappingData.getSecond();

        Map<String, ClassMapping> byVanillaSource = new HashMap<>();
        Map<String, ClassMapping> byBukkit = new HashMap<>();

        bm.byMappedName.forEach((name, cm) -> {
            String bukkitName = cm.mappedName();

            String vanillaObfName = cm.sourceName();
            var vc = vm.byMappedName.get(vanillaObfName);

            String vanillaSourceName = vc.sourceName();

            ClassMapping switched = new ClassMapping(vanillaSourceName, bukkitName,
                    mapFields(vc.fieldsBySourceName.values(), cm),
                    mapMethods(vc.methodsBySourceName.values(), cm));

            byVanillaSource.put(vanillaSourceName, switched);
            byBukkit.put(bukkitName, switched);
        });

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
                    .orElse(vanillaObfName); // fall back to vanilla obf name

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
                String bukkitName = bukkitResolve.methodFromSource(vanillaObfName, vm.parameterTypes())
                        .map(eu.software4you.ulib.spigot.mappings.Mapped::mappedName)
                        .orElse(vanillaObfName); // fall back to vanilla obf name

                Function<MappedClass, Supplier<MappedMethod>> loadTaskGenerator = parent -> () -> new MappedMethod(parent,
                        vm.returnType(), vm.parameterTypes(), vanillaSourceName, bukkitName);
                methods.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
            });

        });

        return methods;
    }
}
