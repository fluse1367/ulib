package eu.software4you.ulib.core.impl.configuration;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.configuration.serialization.*;
import eu.software4you.ulib.core.reflect.ReflectUtil;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.*;
import java.util.function.BiConsumer;

// singleton
public class SerializationAdapters {
    @Getter
    private static final SerializationAdapters instance = new SerializationAdapters();

    private final Set<Pair<Class<?>, Adapter<?>>> registry = new LinkedHashSet<>();
    private final List<BiConsumer<Class<?>, Adapter<?>>> registerHooks = new ArrayList<>();

    private SerializationAdapters() {
        registerAdapter(Serializable.class, new SerializableAdapter());
        registerAdapter(Enum.class, new EnumAdapter());
    }

    public void addHook(BiConsumer<Class<?>, Adapter<?>> registerHook) {
        registerHooks.add(registerHook);

        // run hook for each already registered adapter
        registry.forEach(p -> registerHook.accept(p.getFirst(), p.getSecond()));
    }

    public <T> void registerAdapter(Class<T> serialization, Adapter<? extends T> adapter) {
        if (registry.add(new Pair<>(serialization, adapter))) {
            registerHooks.forEach(hook -> hook.accept(serialization, adapter));
        }
    }

    @Nullable
    public Map<String, ?> attemptSerialization(Object object) {
        var serialized = serialize(object);
        if (serialized == null)
            return null;

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("!", object.getClass().getName());
        map.put("=", serialized);
        return map;
    }

    @Nullable
    public Object attemptDeserialization(Map<?, ?> map, boolean deep) {
        if (!(map.get("!") instanceof String clazz) || !(map.get("=") instanceof Map<?, ?> serialized))
            return null;

        // convert into (string, object)
        Map<String, Object> elements = new LinkedHashMap<>(serialized.size());
        serialized.forEach((key, val) -> {
            Object value = val;

            // try nested deserialization
            if (deep && val instanceof Map<?, ?> m) {
                var newVal = attemptDeserialization(m, true);
                if (newVal != null)
                    value = newVal;
            }

            elements.put(key.toString(), value);
        });

        return deserialize(ReflectUtil.forName(clazz, true).orElseThrow(), elements);
    }

    public Object deserialize(Class<?> clazz, Map<String, Object> elements) {
        var map = Collections.unmodifiableMap(elements);
        for (var en : registry) {
            if (!en.getFirst().isAssignableFrom(clazz))
                continue;

            Object o;
            if ((o = en.getSecond().deserialize((Class) clazz, map)) != null)
                return o;
        }
        return elements;
    }

    public Map<String, Object> serialize(Object object) {
        for (var en : registry) {
            if (!en.getFirst().isInstance(object))
                continue;


            Map<String, Object> map;
            if ((map = ((Adapter<Object>) en.getSecond()).serialize(object)) != null) {
                return map;
            }
        }
        return null;
    }

    public abstract static class Adapter<T> {
        protected abstract T deserialize(Class<? extends T> clazz, Map<String, Object> elements);

        protected abstract Map<String, Object> serialize(T object);
    }

    private static class SerializableAdapter extends Adapter<Serializable<?>> {

        @Override
        protected Serializable<?> deserialize(Class<? extends Serializable<?>> clazz, Map<String, Object> elements) {
            if (!Serializable.class.isAssignableFrom(clazz))
                return null;

            // determine factory
            var types = clazz.getGenericInterfaces();
            if (types.length != 1)
                throw new InvalidFactoryDeclarationException("%s does not declare a factory class".formatted(clazz.getName()));
            var type = types[0];
            if (!(type instanceof ParameterizedType ptype))
                throw new InvalidFactoryDeclarationException("Cannot determine factory class from %s".formatted(type.getClass()));

            types = ptype.getActualTypeArguments();
            if (types.length != 1 || !(types[0] instanceof Class<?> cl))
                throw new InvalidFactoryDeclarationException("Malformed type arguments: %s".formatted(Arrays.toString(types)));

            if (!DeSerializationFactory.class.isAssignableFrom(cl))
                throw new InvalidFactoryDeclarationException("%s does not implement %s".formatted(cl.getName(), DeSerializationFactory.class.getName()));

            Class<? extends DeSerializationFactory<Serializable<?>>> factoryClass = (Class<? extends DeSerializationFactory<Serializable<?>>>) cl;

            // get factory instance
            DeSerializationFactory<Serializable<?>> factory;

            a:
            try {
                try {
                    var getInstance = factoryClass.getMethod("getInstance");

                    if (Modifier.isStatic(getInstance.getModifiers()) && getInstance.getReturnType() == factoryClass) {
                        factory = (DeSerializationFactory<Serializable<?>>) getInstance.invoke(null);
                        break a;
                    }
                } catch (NoSuchMethodException e) {
                    // ignored
                }
                factory = factoryClass.getConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                throw new SerializationException("%s has no default constructor".formatted(factoryClass.getName()));
            } catch (InvocationTargetException | InstantiationException e) {
                throw new SerializationException(e.getCause());
            } catch (IllegalAccessException e) {
                throw new SerializationException(e);
            }

            return factory.deserialize(elements);
        }

        @Override
        protected Map<String, Object> serialize(Serializable<?> object) {
            return object.serialize();
        }
    }

    private static class EnumAdapter extends Adapter<Enum<?>> {

        @Override
        protected Enum<?> deserialize(Class<? extends Enum<?>> clazz, Map<String, Object> elements) {
            return ReflectUtil.getEnumEntry(clazz, (String) elements.get("value")).getValue();
        }

        @Override
        protected Map<String, Object> serialize(Enum<?> object) {
            return Collections.singletonMap("value", object.name());
        }
    }
}
