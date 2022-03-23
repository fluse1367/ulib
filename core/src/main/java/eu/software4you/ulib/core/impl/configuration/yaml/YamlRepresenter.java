package eu.software4you.ulib.core.impl.configuration.yaml;

import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

import java.util.function.BiConsumer;

class YamlRepresenter extends Representer implements BiConsumer<Class<?>, SerializationAdapters.Adapter<?>> {
    private final SerialisationRepresenter representer;

    YamlRepresenter(DumperOptions options) {
        super(options);
        this.representer = new SerialisationRepresenter();
    }

    @Override
    public void accept(Class<?> clazz, SerializationAdapters.Adapter<?> adapter) {
        super.multiRepresenters.put(clazz, representer);
    }

    private class SerialisationRepresenter extends RepresentMap {
        @Override
        public Node representData(Object object) {

            var serialized = SerializationAdapters.getInstance().attemptSerialization(object);
            if (serialized != null) {
                return super.representData(serialized);
            }

            return super.representData(object);
        }
    }
}
