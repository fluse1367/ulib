package eu.software4you.ulib.minecraft.impl.mappings;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.collection.Triple;
import eu.software4you.ulib.core.util.LazyValue;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


final class MixedMapping extends MappingRoot<Pair<BukkitMapping, VanillaMapping>> implements eu.software4you.ulib.minecraft.mappings.MixedMapping {
    MixedMapping(BukkitMapping bm, VanillaMapping vm) {
        super(new Pair<>(bm, vm));
    }

    @Override
    public @NotNull Optional<eu.software4you.ulib.minecraft.mappings.ClassMapping> from(@NotNull Class<?> source) {
        return fromSource(source.getName());
    }

    @Override
    protected Pair<Map<String, ClassMapping>, Map<String, ClassMapping>> generateMappings(Pair<BukkitMapping, VanillaMapping> mappingData) {
        var bukkitMapping = Objects.requireNonNull(mappingData.getFirst());
        var vanillaMapping = Objects.requireNonNull(mappingData.getSecond());

        Map<String, ClassMapping> byVanillaSource = new HashMap<>();
        Map<String, ClassMapping> byBukkit = new HashMap<>();

        bukkitMapping.byMappedName.forEach((name, bukkitClassMapping) -> {
            String bukkitName = bukkitClassMapping.mappedName();

            String vanillaObfName = bukkitClassMapping.sourceName();
            var vc = vanillaMapping.byMappedName.get(vanillaObfName);

            String vanillaSourceName = vc.sourceName();

            ClassMapping switched = new ClassMapping(vanillaSourceName, bukkitName,
                    mapFields(vc.fieldsBySourceName.values(), bukkitClassMapping),
                    mapMethods(vc.methodsBySourceName.values(), bukkitClassMapping));

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
            var vanillaField = loader.get();

            String vanillaSourceName = vanillaField.sourceName();
            String vanillaObfName = vanillaField.mappedName();
            String bukkitName = Optional.ofNullable(bukkitResolve.fieldsBySourceName.get(vanillaObfName))
                    .map(LazyValue::get).map(Mapped::mappedName)
                    .orElse(vanillaObfName); // fall back to vanilla obf name

            Function<MappedClass, Supplier<MappedField>> loadTaskGenerator = parent -> () -> {
                // resolve type
                var type = (MappedClass) bySourceName.get(vanillaField.type().sourceName());
                if (type == null) // fallback
                    type = vanillaField.type();

                return new MappedField(parent, type, vanillaSourceName, bukkitName);
            };
            fields.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
        });

        return fields;
    }

    private List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> mapMethods(
            Collection<LazyValue<List<MappedMethod>>> vanillaMethods, ClassMapping bukkitResolve) {
        // triple: vanillaSourceName, bukkitName, loader
        List<Triple<String, String, Function<MappedClass, Supplier<MappedMethod>>>> methods = new ArrayList<>();

        vanillaMethods.forEach(loader -> Objects.requireNonNull(loader.get()).forEach(vanillaMapping -> {
            String vanillaSourceName = vanillaMapping.sourceName();
            String vanillaObfName = vanillaMapping.mappedName();
            String bukkitName = bukkitResolve.methodFromSource(vanillaObfName, vanillaMapping.parameterTypes())
                    .map(eu.software4you.ulib.minecraft.mappings.Mapped::mappedName)
                    .orElse(vanillaObfName); // fall back to vanilla obf name

            Function<MappedClass, Supplier<MappedMethod>> loadTaskGenerator = parent -> () -> {
                // resolve return type
                var returnType = (MappedClass) bySourceName.get(vanillaMapping.returnType().sourceName());
                if (returnType == null) // fallback
                    returnType = vanillaMapping.returnType();

                // resolve params
                var paramTypes = Stream.of(vanillaMapping.parameterTypes())
                        .map(mappedClass -> {
                            var type = (MappedClass) bySourceName.get(mappedClass.sourceName());
                            return type != null ? type : mappedClass /*fallback*/;
                        })
                        .toArray(MappedClass[]::new);

                return new MappedMethod(parent, returnType, paramTypes, vanillaSourceName, bukkitName);
            };
            methods.add(new Triple<>(vanillaSourceName, bukkitName, loadTaskGenerator));
        }));

        return methods;
    }
}
