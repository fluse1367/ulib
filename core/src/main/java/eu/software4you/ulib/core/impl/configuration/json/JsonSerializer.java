package eu.software4you.ulib.core.impl.configuration.json;

import com.github.cliftonlabs.json_simple.*;
import eu.software4you.ulib.core.impl.configuration.SerializationAdapters;
import lombok.*;

import java.io.*;
import java.util.*;

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
        if (root instanceof JsonArray json) {
            List<? super Object> li = new ArrayList<>(json.size());

            json.forEach(e -> {
                // deserialize each element
                if (e instanceof Map<?, ?> map) {
                    var obj = SerializationAdapters.getInstance().attemptDeserialization(map, true);
                    if (obj != null) {
                        li.add(obj);
                        return;
                    }
                }

                li.add(e);
            });

            doc.put("", li);
            return;
        }

        // other
        doc.put("", root);
    }

    private JsonDocument graph(final JsonDocument parent, final Map<String, Object> localRoot) {

        localRoot.forEach((key, val) -> {

            if (val instanceof Map<?, ?> serialized) {
                // attempt deserialization
                var newVal = SerializationAdapters.getInstance().attemptDeserialization(serialized, true);
                if (newVal != null) {
                    // deserialization success
                    val = newVal;
                } else {
                    // deserialization failure
                    // it's still a map so construct sub

                    // convert map
                    Map<String, Object> elements = new LinkedHashMap<>(serialized.size());
                    serialized.forEach((k, v) -> elements.put(k.toString(), v));

                    // graph
                    val = graph(parent.constructSub(key), elements);
                }
            }

            parent.put(key, val);
        });

        return parent;
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
