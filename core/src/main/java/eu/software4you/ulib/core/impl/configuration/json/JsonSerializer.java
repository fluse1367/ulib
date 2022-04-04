package eu.software4you.ulib.core.impl.configuration.json;

import com.github.cliftonlabs.json_simple.*;
import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import lombok.*;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonSerializer {

    @Getter
    private static final JsonSerializer instance = new JsonSerializer();

    public JsonDocument createNew() {
        return new JsonDocument(this);
    }

    public JsonDocument deserialize(Reader reader) throws IOException {
        var doc = createNew();
        doc.reinit(reader).rethrow();
        return doc;
    }

    void deserialize(Reader reader, JsonDocument doc) throws JsonException {

        var root = Jsoner.deserialize(reader);
        if (root == null)
            return;

        // clear doc
        doc.clear();

        // data tree
        if (root instanceof JsonObject json) {
            graph(doc, json);
            return;
        }

        // list
        if (root instanceof JsonArray array) {
            doc.put("", process(doc, "", array));
            return;
        }

        // other
        doc.put("", root);
    }

    private JsonDocument graph(final JsonDocument parent, final JsonObject localRoot) {
        localRoot.forEach((key, val) -> parent.put(key, process(parent, key, val)));
        return parent;
    }

    private Object process(final JsonDocument parent, final String key, final Object val) {
        if (val instanceof JsonObject serialized) {
            // attempt deserialization
            var newVal = SerializationAdapters.getInstance().attemptDeserialization(serialized, true);
            if (newVal != null) {
                // deserialization success
                return newVal;
            }
            // deserialization failure
            // it's still a map so construct sub

            // graph
            return graph(parent.constructSub(key), serialized);
        }


        if (val instanceof JsonArray array) {
            // process each element of array
            return array.stream()
                    .map(o -> process(parent, key, o))
                    .toList();
        }

        // value is something else
        return val;
    }

    void serialize(Writer writer, JsonDocument doc) throws IOException {
        Jsoner.serializeStrictly(represent(doc), writer);
    }

    private Map<String, Object> represent(final JsonDocument doc) {
        var children = doc.children();
        Map<String, Object> representation = new LinkedHashMap<>(children.size());

        children.forEach((key, val) -> {
            if (val instanceof JsonDocument sub) {
                representation.put(key, represent(sub));
                return;
            }

            // attempt serialization
            var serialized = SerializationAdapters.getInstance().attemptSerialization(val);
            if (serialized != null) {
                val = serialized;
            }

            representation.put(key, val);
        });

        return representation;
    }
}
