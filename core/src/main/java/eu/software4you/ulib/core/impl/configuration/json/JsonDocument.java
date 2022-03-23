package eu.software4you.ulib.core.impl.configuration.json;

import eu.software4you.ulib.core.configuration.JsonConfiguration;
import eu.software4you.ulib.core.impl.configuration.ConfigurationBase;
import eu.software4you.ulib.core.util.Expect;

import java.io.*;
import java.util.Map;

public class JsonDocument extends ConfigurationBase<JsonDocument> implements JsonConfiguration {

    private final JsonSerializer serializer;

    // construct as empty root
    JsonDocument(JsonSerializer serializer) {
        super();
        this.serializer = serializer;
    }

    // construct as sub
    JsonDocument(JsonSerializer serializer, JsonDocument root, JsonDocument parent, String key) {
        super(root, parent, key);
        this.serializer = serializer;
    }

    @Override
    protected JsonDocument constructSub(String key) {
        return new JsonDocument(serializer, getRoot(), this, key);
    }

    // IO

    @Override
    public Expect<Void, IOException> reinit(Reader reader) {
        return Expect.compute(() -> {
            purge();
            var opCaught = Expect.compute(() -> serializer.deserialize(reader, this)).getCaught();
            if (opCaught.isPresent())
                throw new IOException(opCaught.get());
        });
    }

    @Override
    public Expect<Void, IOException> dump(Writer writer) {
        return Expect.compute(() -> serializer.serialize(writer, this));
    }

    @Override
    public void purge() {
        clear();
    }


    // serializer access

    Map<String, Object> children() {
        return children;
    }

    void put(String key, Object val) {
        children.put(key, val);
    }

    void clear() {
        children.clear();
    }
}
