package eu.software4you.ulib.core.impl.configuration;

import eu.software4you.ulib.core.api.common.collection.Pair;
import eu.software4you.ulib.core.api.configuration.serialization.DeSerializationFactory;
import eu.software4you.ulib.core.api.configuration.serialization.InvalidFactoryDeclarationException;
import eu.software4you.ulib.core.api.configuration.serialization.Serializable;
import eu.software4you.ulib.core.api.configuration.serialization.SerializationException;
import eu.software4you.ulib.core.api.reflect.ReflectUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.function.BiConsumer;

public class SerializationAdapters {
    private final Set<Pair<Class<?>, Adapter<?>>> registry = new LinkedHashSet<>();
    private final BiConsumer<Class<?>, Adapter<?>> registerHook;

    public SerializationAdapters(BiConsumer<Class<?>, Adapter<?>> registerHook) {
        this.registerHook = registerHook;
        registerAdapter(Serializable.class, new SerializableAdapter());
        registerAdapter(Enum.class, new EnumAdapter());
    }

    public <T> void registerAdapter(Class<T> serialisation, Adapter<? extends T> adapter) {
        if (registry.add(new Pair<>(serialisation, adapter))) {
            registerHook.accept(serialisation, adapter);
        }
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
            return ReflectUtil.getEnumEntry(clazz, (String) elements.get("value")).orElse(null);
        }

        @Override
        protected Map<String, Object> serialize(Enum<?> object) {
            return Collections.singletonMap("value", object.name());
        }
    }
}
