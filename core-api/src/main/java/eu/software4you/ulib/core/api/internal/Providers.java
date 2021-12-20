package eu.software4you.ulib.core.api.internal;

import eu.software4you.ulib.core.api.configuration.yaml.ExtYamlSub;
import eu.software4you.ulib.core.api.database.sql.ColumnBuilder;
import eu.software4you.ulib.core.api.database.sql.DataType;

import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;

public final class Providers {
    private static final Map<Class<?>, Object> SERVICES = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <S> S get(Class<S> service) throws IllegalArgumentException {
        return get(service, Providers.class.getClassLoader());
    }

    public static <S> S get(Class<S> service, ClassLoader cl) throws IllegalArgumentException {
        if (!SERVICES.containsKey(service)) {
            var module = Providers.class.getModule();
            if (!module.canUse(service))
                module.addUses(service);

            var loader = ServiceLoader.load(service, cl);
            var first = loader.findFirst();
            if (first.isEmpty()) {
                throw new IllegalArgumentException("No service provider found for " + service.getName());
            }
            SERVICES.put(service, first.get());
        }

        return (S) SERVICES.get(service);
    }

    public interface ProviderColumnBuilder {
        <T> ColumnBuilder<T> provide(Class<T> t, String name, DataType dataType);
    }

    public interface ProviderExtYamlSub extends ServiceLoader.Provider<ExtYamlSub> {
    }

}
