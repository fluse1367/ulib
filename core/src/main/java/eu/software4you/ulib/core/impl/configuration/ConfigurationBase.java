package eu.software4you.ulib.core.impl.configuration;

import eu.software4you.ulib.core.collection.Pair;
import eu.software4you.ulib.core.common.Keyable;
import eu.software4you.ulib.core.configuration.Configuration;
import eu.software4you.ulib.core.util.Conversions;
import eu.software4you.ulib.core.util.Expect;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public abstract class ConfigurationBase<R extends ConfigurationBase<R>> implements Configuration, Keyable<String> {
    protected static final String PATH_SEPARATOR = ".";

    // key -> node (data or sub)
    protected final Map<String, Object> children = new LinkedHashMap<>();

    @Getter
    @NotNull
    private final R root, parent;
    @Getter
    @NotNull
    private final String key; // key of this sub

    // root constructor
    protected ConfigurationBase() {
        this.root = (R) this;
        this.parent = (R) this;
        this.key = "";
    }

    // child constructor
    protected ConfigurationBase(@NotNull R root, @NotNull R parent, @NotNull String key) {
        this.root = root;
        this.parent = parent;
        this.key = key;
    }

    // - direct data access -

    private <T> T _get(String path) {
        return this.<T>get(path).orElse(null);
    }

    @Override
    @NotNull
    public <T> Optional<T> get(@NotNull String path) {
        Objects.requireNonNull(path, "Path may not be null");

        return resolveValue(path)
                .map(value -> {
                    //noinspection unchecked
                    return Expect.<T, ClassCastException>compute(() -> (T) value).getValue();
                });
    }

    @Override
    public @NotNull <T> Optional<T> get(@NotNull Class<T> clazz, @NotNull String path) {
        return resolveValue(path)
                .filter(clazz::isInstance)
                .map(clazz::cast);
    }

    @Override
    public @NotNull <T> Optional<List<T>> list(@NotNull Class<T> clazz, @NotNull String path) {
        return get(path)
                .map(val -> val.getClass().isArray() ? Arrays.asList((Object[]) val)
                        : ((val instanceof List<?> li) ? li : null))
                .filter(li -> li.stream().allMatch(clazz::isInstance))
                .map(li -> (List<T>) li);
    }

    @Override
    public @NotNull Optional<Boolean> bool(@NotNull String path) {
        return Conversions.tryBoolean(_get(path)).toOptional();
    }

    @Override
    public @NotNull Optional<Float> dec32(@NotNull String path) {
        return Conversions.tryFloat(_get(path)).toOptional();
    }

    @Override
    public @NotNull Optional<Double> dec64(@NotNull String path) {
        return Conversions.tryDouble(_get(path)).toOptional();
    }

    @Override
    public @NotNull Optional<Integer> int32(@NotNull String path) {
        return Conversions.tryInt(_get(path)).toOptional();
    }

    @Override
    public @NotNull Optional<Long> int64(@NotNull String path) {
        return Conversions.tryLong(_get(path)).toOptional();
    }

    @Override
    public @NotNull Collection<String> getKeys(boolean deep) {
        Set<String> keys = new LinkedHashSet<>();

        children.forEach((key, value) -> {

            // do not add a sub as key, only it's values
            if (deep && value instanceof ConfigurationBase<?> doc) {
                String prefix = this.key.isEmpty() ? "" : this.key + PATH_SEPARATOR;

                doc.getKeys(true).stream()
                        .map(k -> String.format("%s%s%s%s", prefix, doc.key, PATH_SEPARATOR, k))
                        .forEach(keys::add);
            } else {
                keys.add(key);
            }

        });

        return keys;
    }

    @Override
    public @NotNull Map<String, Object> getValues(boolean deep) {
        Map<String, Object> values = new LinkedHashMap<>();

        children.forEach((key, value) -> {

            // do not add a sub as key, only it's values
            if (deep && value instanceof ConfigurationBase doc) {
                String prefix = this.key.isEmpty() ? "" : this.key + PATH_SEPARATOR;

                doc.getValues(true).forEach((k, v) -> values.put(String.format("%s%s%s%s", prefix, doc.key, PATH_SEPARATOR, k), v));
            } else {
                values.put(key, value);
            }

        });

        return values;
    }

    @Override
    public void set(@NotNull String fullPath, Object value) {
        Pair<R, String> r;
        if (value != null) {
            r = resolveFull(fullPath);
        } else {
            var op = resolve(fullPath);
            if (op.isEmpty())
                return;
            r = op.get();
        }

        var doc = r.getFirst();
        var key = r.getSecond();
        var isActuallyNew = doc.children.containsKey(key);
        doc.children.put(key, value);
        doc.placedNewValue(key, value, isActuallyNew);
    }

    // - sub access -

    @Override
    @NotNull
    public Optional<R> getSub(@NotNull String path) {
        return resolveValue(path)
                .filter(ConfigurationBase.class::isInstance)
                .map(sub -> Expect.compute(() -> (R) sub).getValue());
    }

    @Override
    public @NotNull R createSub(@NotNull String fullPath) {
        var r = resolveFull(fullPath);
        return ((ConfigurationBase<R>) r.getFirst()).putNewSub(r.getSecond());
    }

    @Override
    public @NotNull R subAndCreate(@NotNull String path) {
        var opSub = getSub(path);
        return opSub.orElseGet(() -> createSub(path));
    }

    @Override
    public @NotNull Collection<R> getSubs() {
        return children.values().stream()
                .filter(ConfigurationBase.class::isInstance)
                .map(sub -> Expect.compute(() -> (R) sub).getValue())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // - checks -

    @Override
    public boolean isSet(@NotNull String path) {
        return resolveValue(path)
                .map(obj -> !(obj instanceof ConfigurationBase))
                .orElse(false);
    }

    @Override
    public boolean contains(@NotNull String path) {
        return resolveValue(path).isPresent();
    }

    @Override
    public boolean isSub(@NotNull String path) {
        return resolveValue(path)
                .map(obj -> obj instanceof ConfigurationBase)
                .orElse(false);
    }

    @Override
    public boolean isRoot() {
        return this == root;
    }


    // - resolvers -

    // resolves the full path into a valid sub and key
    // creates new subs if necessary
    private Pair<R, String> resolveFull(final String fullPath) {
        return resolve(fullPath, true)
                .orElseThrow(() -> new IllegalStateException("Resolution failure"));
    }

    // attempts to resolve the full path into a valid sub and a key
    // if `createSub` is true, resolution success is guaranteed
    // note that the key is not guaranteed to exist in the sub
    // returns pair: (sub, key)
    protected final Optional<Pair<R, String>> resolve(final String fullPath, boolean createSub) {
        String restPath = fullPath;
        ConfigurationBase<R> doc = this;
        int i;
        while ((i = restPath.indexOf(PATH_SEPARATOR)) >= 0) {
            String key = restPath.substring(0, i);
            restPath = restPath.substring(i + PATH_SEPARATOR.length());

            if (doc.children.get(key) instanceof ConfigurationBase<?> cb && cb.getClass() == getClass()) {
                doc = (ConfigurationBase<R>) cb;
                // sub does exist, continue with that
                continue;
            }

            // sub does not exist
            if (!createSub) {
                // not allowed to create a new sub or overwrite existing value with one
                // indicate failure
                return Optional.empty();
            }

            // create/overwrite
            doc = doc.putNewSub(key);
        }

        // successfully resolved
        return Optional.of(new Pair<>((R) doc, restPath));
    }

    // attempts to resolve the full path into a valid sub and a key
    // resolution success is not guaranteed
    // note that the key is not guaranteed to exist in the sub
    // returns pair: (sub, key)
    protected final Optional<Pair<R, String>> resolve(final String fullPath) {
        return resolve(fullPath, false);
    }

    // attempts to resolve the full path and fetch the child object from a certain sub
    protected final Optional<Object> resolveValue(final String fullPath) {
        return resolve(fullPath)
                // pair: (doc, key)
                .map(pair -> pair.getFirst().children.get(pair.getSecond()));
    }

    // - misc -

    private R putNewSub(String key) {
        var sub = constructSub(key);
        children.put(key, sub);
        return sub;
    }

    // - abstraction -

    // constructs a new sub with the specified key
    protected abstract R constructSub(String key);

    // called when a new value has been placed at the specified key, while overwriting any other associated value
    // the key is not resolved
    // param genuinelyNew indicates if a value has been overwritten or is actually 'new'
    protected void placedNewValue(String key, Object value, boolean genuinelyNew) {
        // to be overwritten
    }

}
