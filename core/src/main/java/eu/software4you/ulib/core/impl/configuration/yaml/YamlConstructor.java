package eu.software4you.ulib.core.impl.configuration.yaml;

import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import lombok.SneakyThrows;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.*;

import java.util.Map;

class YamlConstructor extends SafeConstructor {
    private final SerializationConstruct serializationConstruct;

    YamlConstructor(LoaderOptions loadingConfig) {
        super(loadingConfig);
        serializationConstruct = new SerializationConstruct();
    }

    Object construct(Node node) {
        return construct(node, Object.class);
    }

    <T> T construct(Node node, Class<T> type) {
        if (node != null && !Tag.NULL.equals(node.getTag())) {
            if (Object.class != type) {
                node.setTag(new Tag(type));
            } else if (rootTag != null) {
                node.setTag(rootTag);
            }
            return (T) constructDocument(node);
        } else {
            Construct construct = yamlConstructors.get(Tag.NULL);
            return (T) construct.construct(node);
        }
    }

    boolean isSerialized(MappingNode node) {
        return ((ScalarNode) node.getValue().get(0).getKeyNode()).getValue().equals("!")
               && ((ScalarNode) node.getValue().get(1).getKeyNode()).getValue().equals("=");
    }

    Construct getSerializationConstruct() {
        return serializationConstruct;
    }

    private class SerializationConstruct extends ConstructYamlMap {
        @SneakyThrows
        @Override
        public Object construct(Node node) {
            var map = (Map<?, ?>) super.construct(node);

            if (node.isTwoStepsConstruction())
                return null;

            // not deep bc nested elements are already handled by snakeyaml
            return SerializationAdapters.getInstance().attemptDeserialization(map, false);
        }
    }
}
