package eu.software4you.ulib.impl.configuration.yaml;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

class YamlConstructor extends SafeConstructor {
    YamlConstructor(LoaderOptions loadingConfig) {
        super(loadingConfig);
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
}
