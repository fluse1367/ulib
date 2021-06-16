package eu.software4you.ulib.impl.configuration.yaml;

import eu.software4you.common.collection.Pair;
import eu.software4you.configuration.yaml.ExtYamlSub;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.nodes.Node;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ExtYamlDocument extends YamlDocument implements ExtYamlSub {
    // constructor for empty root
    protected ExtYamlDocument(YamlSerializer serializer) {
        super(serializer);
    }

    // constructor for deserialized root
    protected ExtYamlDocument(YamlSerializer serializer, Reader reader) throws IOException {
        super(serializer, reader);
    }

    // constructor for sub
    protected ExtYamlDocument(YamlDocument parent, String key, Node valueNode) {
        super(parent, key, valueNode);
    }

    @Override
    protected ExtYamlDocument constructChild(String key, Node valueNode) {
        return new ExtYamlDocument(this, key, valueNode);
    }

    @Override
    public @NotNull <T> Optional<T> get2(@NotNull String path, @Nullable T def) throws IllegalArgumentException {
        return resolveChild(path)
                .map(Pair::getSecond)
                .map(value -> {
                    try {
                        return (T) value;
                    } catch (ClassCastException e) {
                        return def;
                    }
                });
    }

    @Override
    public @NotNull ExtYamlSub subAndCreate(@NotNull String path) {
        val sub = getSub(path);
        return sub != null ? sub : createSub(path);
    }

    @Override
    public boolean bool(@NotNull String path, boolean def) {
        return this.<Boolean>get2(path).orElse(def);
    }

    @Override
    public int int32(@NotNull String path, int def) {
        return this.<Integer>get2(path).orElse(def);
    }

    @Override
    public long int64(@NotNull String path, long def) {
        return this.<Long>get2(path).orElse(def);
    }

    @Override
    public float dec32(@NotNull String path, float def) {
        return this.<Float>get2(path).orElse(def);
    }

    @Override
    public double dec64(@NotNull String path, double def) {
        return this.<Double>get2(path).orElse(def);
    }

    @Override
    public <T> @Nullable T get(@NotNull Class<T> clazz, @NotNull String path, @Nullable T def, Object... replacements) {
        Object val;
        if (clazz.equals(String.class)) {
            val = this.<String>get2(path).orElse(def != null ? def.toString() : null);
        } else {
            val = get2(path).orElse(def);
        }
        if (val == null)
            return def;

        if (val instanceof String)
            val = replacements.length > 0 ? String.format((String) val, replacements) : (String) val;
        else if (val instanceof Number) {
            if (clazz == Integer.class && !(val instanceof Integer))
                return clazz.cast(Integer.parseInt(String.valueOf(val)));
            if (clazz == Float.class && !(val instanceof Float))
                return clazz.cast(Float.parseFloat(String.valueOf(val)));
            if (clazz == Double.class && !(val instanceof Double))
                return clazz.cast(Double.parseDouble(String.valueOf(val)));
            if (clazz == Long.class && !(val instanceof Long))
                return clazz.cast(Long.parseLong(String.valueOf(val)));
        }
        if (!clazz.isInstance(val)) {
            return thrOrDef(def, clazz, val, path);
        }
        return (T) val;
    }

    @Override
    public @Nullable <T> List<T> list(@NotNull Class<T> clazz, @NotNull String path, @Nullable List<T> def, Object... replacements) {
        Object val = get(path);
        if (val == null)
            return def;

        if (!(val instanceof List<?>)) {
            thrOrDef(def, List.class, val, path);
        }

        List<T> list = new ArrayList<>();
        List<?> myList = (List<?>) val;

        for (Object o : myList) {
            if (!clazz.isInstance(o))
                return thrOrDef(def, clazz, o, path);
            if (o instanceof String)
                o = replacements.length > 0 ? String.format((String) o, replacements) : (String) o;
            list.add((T) o);
        }
        return list;
    }

    private <T> T thrOrDef(T def, Class<?> clazz, Object val, String path) {
        switch (getRoot().conversionPolicy) {
            case RETURN_DEFAULT:
                return def;
            case THROW_EXCEPTION:
                throw new IllegalArgumentException(String.format("Cannot convert %s to type %s (%s)",
                        val.getClass().getName(), clazz.getName(), path));
        }
        throw new IllegalStateException(); // make compiler happy
    }

    @Override
    public @Nullable ExtYamlDocument getSub(@NotNull String path) {
        return (ExtYamlDocument) super.getSub(path);
    }

    @Override
    public @NotNull ExtYamlDocument createSub(@NotNull String fullPath) {
        return (ExtYamlDocument) super.createSub(fullPath);
    }

    @Override
    public @NotNull Collection<? extends ExtYamlDocument> getSubs() {
        return (Collection<? extends ExtYamlDocument>) super.getSubs();
    }
}
